package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    List<Configuration> findByConfigNameIn(List<String> configName);

    Configuration findByConfigName(String name);

    @Query(value = "select c.configValue from Configuration c where c.configName=:configName")
    String findConfigValueByConfigName(@Param("configName") String configName);
}
