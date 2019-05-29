package uk.gov.ons.census.caseapisvc.model.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cases")
public class Case {

  @Id
  @SequenceGenerator(name = "caseRefGenerator", initialValue = 10000000)
  @GeneratedValue(generator = "caseRefGenerator", strategy = GenerationType.SEQUENCE)
  private long caseRef;

  @Column private UUID caseId;

  @Column private String arid;

  @Column private String estabArid;

  @Column private String estabType;

  @Column private String uprn;

  @Column private String addressType;

  @Column private OffsetDateTime createdDateTime;

  @Column private String addressLine1;

  @Column private String addressLine2;

  @Column private String addressLine3;

  @Column private String townName;

  @Column private String postcode;

  @Column private String organisationName;

  @Column private String addressLevel;

  @Column private String abpCode;

  @Column private String rgn;

  @Column private String latitude;

  @Column private String longitude;

  @Column private String oa;

  @Column private String lsoa;

  @Column private String msoa;

  @Column private String lad;

  @Column
  @Enumerated(EnumType.STRING)
  private CaseState state;

  @OneToMany(mappedBy = "caze")
  List<UacQidLink> uacQidLinks;
}
