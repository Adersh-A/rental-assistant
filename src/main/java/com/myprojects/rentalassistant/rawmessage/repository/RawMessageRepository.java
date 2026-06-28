package com.myprojects.rentalassistant.rawmessage.repository;

import com.myprojects.rentalassistant.rawmessage.entity.RawMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMessageRepository extends JpaRepository<RawMessage, Long> {

    boolean existsByMessageHash(String messageHash);
}
