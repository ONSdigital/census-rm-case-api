package uk.gov.ons.census.caseapisvc.utility;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.dto.EventDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.Event;

@Component
public class CaseSvcBeanMapper extends ConfigurableMapper {

  protected final void configure(final MapperFactory factory) {
    factory
        .classMap(Case.class, CaseContainerDTO.class)
        .field("caseId", "id")
        .field("addressType", "caseType")
        .field("rgn", "region")
        .byDefault()
        .register();

    factory.classMap(Event.class, EventDTO.class).byDefault().register();
  }
}
