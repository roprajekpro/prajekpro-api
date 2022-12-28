package com.prajekpro.api.repository;

import com.prajekpro.api.domain.TimeSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotsRepository extends JpaRepository<TimeSlots,Long> {
    List<TimeSlots> findByActiveStatus(int i);
}
