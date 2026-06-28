package com.myprojects.rentalassistant.whatsapp.parser;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WhatsAppChatParser {

    private static final Pattern MESSAGE_HEADER = Pattern.compile(
            "^(?<date>\\d{1,2}/\\d{1,2}/\\d{2,4}),\\s*(?<time>\\d{1,2}:\\d{2})[\\s\\u202F\\u00A0]*(?<period>[aApP]\\.?[mM]\\.?)?\\s+-\\s+(?<body>.*)$"
    );

    private static final DateTimeFormatter TWO_DIGIT_YEAR_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral('/')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral('/')
            .appendValueReduced(ChronoField.YEAR, 2, 2, 2000)
            .toFormatter(Locale.ENGLISH);

    private static final DateTimeFormatter FOUR_DIGIT_YEAR_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("d/M/uuuu", Locale.ENGLISH);

    private static final DateTimeFormatter TWELVE_HOUR_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("h:mm a", Locale.ENGLISH);

    private static final DateTimeFormatter TWENTY_FOUR_HOUR_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("H:mm", Locale.ENGLISH);

    public List<ParsedWhatsAppMessage> parse(String chatText) {
        String normalizedChatText = chatText.startsWith("\uFEFF") ? chatText.substring(1) : chatText;
        List<ParsedWhatsAppMessage> messages = new ArrayList<>();
        CurrentMessage current = null;

        for (String line : normalizedChatText.lines().toList()) {
            Matcher matcher = MESSAGE_HEADER.matcher(line);
            if (matcher.matches()) {
                if (current != null) {
                    messages.add(current.toParsedMessage());
                }
                current = startMessage(matcher).orElse(null);
            } else if (current != null) {
                current.appendLine(line);
            }
        }

        if (current != null) {
            messages.add(current.toParsedMessage());
        }

        return messages;
    }

    private Optional<CurrentMessage> startMessage(Matcher matcher) {
        String body = matcher.group("body");
        int senderSeparator = body.indexOf(": ");
        if (senderSeparator < 0) {
            return Optional.empty();
        }

        String sender = body.substring(0, senderSeparator).trim();
        String messageText = body.substring(senderSeparator + 2).strip();
        if (sender.isBlank()) {
            return Optional.empty();
        }

        LocalDateTime timestamp = parseTimestamp(
                matcher.group("date"),
                matcher.group("time"),
                matcher.group("period")
        );

        return Optional.of(new CurrentMessage(sender, timestamp, messageText));
    }

    private LocalDateTime parseTimestamp(String datePart, String timePart, String periodPart) {
        LocalDate date = parseDate(datePart);
        LocalTime time = parseTime(timePart, periodPart);

        return LocalDateTime.of(date, time);
    }

    private LocalDate parseDate(String datePart) {
        DateTimeFormatter formatter = datePart.substring(datePart.lastIndexOf('/') + 1).length() == 2
                ? TWO_DIGIT_YEAR_DATE_FORMATTER
                : FOUR_DIGIT_YEAR_DATE_FORMATTER;

        return LocalDate.parse(datePart, formatter);
    }

    private LocalTime parseTime(String timePart, String periodPart) {
        if (periodPart == null || periodPart.isBlank()) {
            return LocalTime.parse(timePart, TWENTY_FOUR_HOUR_TIME_FORMATTER);
        }

        String normalizedPeriod = periodPart.replace(".", "").toUpperCase(Locale.ENGLISH);
        try {
            return LocalTime.parse(timePart + " " + normalizedPeriod, TWELVE_HOUR_TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid WhatsApp message time: " + timePart + " " + periodPart, ex);
        }
    }

    private static final class CurrentMessage {

        private final String sender;
        private final LocalDateTime timestamp;
        private final StringBuilder messageText;

        private CurrentMessage(String sender, LocalDateTime timestamp, String messageText) {
            this.sender = sender;
            this.timestamp = timestamp;
            this.messageText = new StringBuilder(messageText);
        }

        private void appendLine(String line) {
            messageText.append(System.lineSeparator()).append(line);
        }

        private ParsedWhatsAppMessage toParsedMessage() {
            return new ParsedWhatsAppMessage(sender, timestamp, messageText.toString().strip());
        }
    }
}
