package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ApptDocsRepository extends JpaRepository<ApptDocs, Long> {
    List<ApptDocs> findByAppointmentDetails_Id(Long id);
}
