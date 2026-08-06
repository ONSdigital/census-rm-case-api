package uk.gov.ons.census.caseapisvc.testutils;

import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.ons.census.caseapisvc.model.dto.EventHeaderDTO;
import uk.gov.ons.census.caseapisvc.model.repository.*;
import uk.gov.ons.census.common.model.entity.*;

@Component
@ActiveProfiles("test")
public class JunkDataHelper {
  private static final Random RANDOM = new Random();

  @Autowired private CaseRepository caseRepository;
  @Autowired private CollectionExerciseRepository collectionExerciseRepository;
  @Autowired private SurveyRepository surveyRepository;

  public Case setupJunkCase() {
    Case junkCase = new Case();
    junkCase.setId(UUID.randomUUID());
    junkCase.setInvalid(false);
    junkCase.setCollectionExercise(setupJunkCollex());
    junkCase.setCaseRef(RANDOM.nextLong());
    junkCase.setAbpCode("abp");
    junkCase.setAddressLevel("U");
    junkCase.setAddressLine1("10 test street");
    junkCase.setAddressType("HH");
    junkCase.setCaseType("HH");
    junkCase.setEstabType("HOUSEHOLD");
    junkCase.setEstabUprn("000000");
    junkCase.setPrintBatch("1");
    junkCase.setFieldCoordinatorId("fcor_id");
    junkCase.setFieldOfficerId("foff_id");
    junkCase.setHtcDigital("1");
    junkCase.setHtcWillingness("1");
    junkCase.setLad("0000");
    junkCase.setLatitude("0.0.0.0.0.0");
    junkCase.setLongitude("0.1278");
    junkCase.setLsoa("0000");
    junkCase.setRegion("EN");
    junkCase.setOa("0000");
    junkCase.setMsoa("0000");
    junkCase.setPostcode("CFXX XXX");
    junkCase.setTreatmentCode("BLJF_FEJG");
    junkCase.setUprn("000000");
    junkCase.setTownName("Best Town");
    caseRepository.save(junkCase);

    return junkCase;
  }

  public CollectionExercise setupJunkCollex() {
    Survey junkSurvey = new Survey();
    junkSurvey.setId(UUID.randomUUID());
    junkSurvey.setName("Junk survey");
    junkSurvey.setSampleSeparator('j');
    surveyRepository.saveAndFlush(junkSurvey);

    CollectionExercise junkCollectionExercise = new CollectionExercise();
    junkCollectionExercise.setId(UUID.randomUUID());
    junkCollectionExercise.setName("Junk collex");
    junkCollectionExercise.setSurvey(junkSurvey);
    junkCollectionExercise.setReference("MVP012021");
    junkCollectionExercise.setStartDate(OffsetDateTime.now());
    junkCollectionExercise.setEndDate(OffsetDateTime.now().plusDays(2));
    junkCollectionExercise.setMetadata(null);
    collectionExerciseRepository.saveAndFlush(junkCollectionExercise);

    return junkCollectionExercise;
  }

  public void junkify(EventHeaderDTO eventHeaderDTO) {
    if (eventHeaderDTO.getChannel() == null) {
      eventHeaderDTO.setChannel("Junk");
    }

    if (eventHeaderDTO.getSource() == null) {
      eventHeaderDTO.setSource("Junk");
    }

    if (eventHeaderDTO.getCorrelationId() == null) {
      eventHeaderDTO.setCorrelationId(UUID.randomUUID());
    }

    if (eventHeaderDTO.getMessageId() == null) {
      eventHeaderDTO.setMessageId(UUID.randomUUID());
    }

    if (eventHeaderDTO.getDateTime() == null) {
      eventHeaderDTO.setDateTime(OffsetDateTime.now());
    }
  }
}
