package uk.gov.ons.census.caseapisvc.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;

// The bidirectional relationships with other entities can cause stack overflows with the default
// toString
@ToString(onlyExplicitlyIncluded = true)
@Data
@Entity
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Table(
    name = "cases",
    indexes = {
      @Index(name = "cases_case_ref_idx", columnList = "case_ref"),
      @Index(name = "lsoa_idx", columnList = "lsoa"),
      @Index(name = "uprn_idx", columnList = "uprn"),
    })
public class Case {

  @Id private UUID caseId;

  // This incrementing column allows us to generate a pseudorandom unique (non-colliding) caseRef
  @Column(columnDefinition = "serial")
  @Generated(GenerationTime.INSERT)
  private int secretSequenceNumber;

  @Column(name = "case_ref")
  private Long caseRef;

  @Column private String uprn;

  @Column private String estabUprn;

  @Column private String caseType;

  @Column private String addressType;

  @Column private String estabType;

  @Column private String addressLevel;

  @Column private String abpCode;

  @Column private String organisationName;

  @Column private String addressLine1;

  @Column private String addressLine2;

  @Column private String addressLine3;

  @Column private String townName;

  @Column private String postcode;

  @Column private String latitude;

  @Column private String longitude;

  @Column private String oa;

  @Column(name = "lsoa")
  private String lsoa;

  @Column private String msoa;

  @Column private String lad;

  @Column private String region;

  @Column private String htcWillingness;

  @Column private String htcDigital;

  @Column private String fieldCoordinatorId;

  @Column private String fieldOfficerId;

  @Column private String treatmentCode;

  @Column private Integer ceExpectedCapacity;

  @Column(nullable = false)
  private int ceActualResponses;

  @Column private UUID collectionExerciseId;

  @Column private UUID actionPlanId;

  @Column(nullable = false)
  private String survey;

  @Column(columnDefinition = "timestamp with time zone")
  @CreationTimestamp
  private OffsetDateTime createdDateTime;

  @OneToMany(mappedBy = "caze")
  List<UacQidLink> uacQidLinks;

  @OneToMany(mappedBy = "caze")
  List<Event> events;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
  private boolean receiptReceived;

  @Enumerated(EnumType.STRING)
  @Column
  private RefusalType refusalReceived;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
  private boolean addressInvalid;

  @Column(columnDefinition = "timestamp with time zone")
  @UpdateTimestamp
  private OffsetDateTime lastUpdated;

  @Column(columnDefinition = "BOOLEAN DEFAULT false")
  private boolean handDelivery;

  @Column(columnDefinition = "BOOLEAN DEFAULT false")
  private boolean skeleton;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private CaseMetadata metadata;

  @Column private String printBatch;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
  private boolean surveyLaunched;
}
