package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface StaticContentRepository extends JpaRepository<StaticContent, Long> {
    StaticContent findByContentId(String contentId);
}
