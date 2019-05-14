delete from casev2.event;

delete from casev2.uac_qid_link;

delete from casev2.cases;

INSERT INTO casev2.cases (case_ref, abp_code, action_plan_id, address_level, address_line1, address_line2,
                          address_line3, address_type, arid, case_id, collection_exercise_id, estab_arid, estab_type,
                          htc_digital, htc_willingness, lad, latitude, longitude, lsoa, msoa, oa, organisation_name,
                          postcode, rgn, state, town_name, treatment_code, uprn)
VALUES (10000000, 'RD06', 'e1f27112-107c-41e9-99a7-01abf343f76c', 'U', 'Flat 56 Francombe House', 'Commercial Road',
        'any addressLine3', 'HH', 'DDR190314000000195675', 'c0d4f87d-9d19-4393-80c9-9eb94f69c460',
        '045bbaf5-f219-4306-9593-0a7520eea2a8', 'DDR190314000000113740', 'Household', '5', '1', 'E06000023',
        '51.4463421', '-2.5924477', 'E01014540', 'E02003043', 'E00073438', null, 'XX1 0XX', 'E12000009', 'ACTIONABLE',
        'Windleybury', 'HH_LF3R2E', '123456789012345');

INSERT INTO casev2.cases (case_ref, abp_code, action_plan_id, address_level, address_line1, address_line2,
                          address_line3, address_type, arid, case_id, collection_exercise_id, estab_arid, estab_type,
                          htc_digital, htc_willingness, lad, latitude, longitude, lsoa, msoa, oa, organisation_name,
                          postcode, rgn, state, town_name, treatment_code, uprn)
VALUES (10000001, 'RD06', 'e1f27112-107c-41e9-99a7-01abf343f76c', 'U', 'First And Second Floor Flat',
        '39 Cranbrook Road', 'any addressLine3', 'HH', 'DDR190314000000239595', '16d79007-9224-448a-9e59-944d9d153fa1',
        '045bbaf5-f219-4306-9593-0a7520eea2a8', 'DDR190314000000060908', 'Household', '4', '2', 'E06000023',
        '51.4721166', '-2.5970579', 'E01014669', 'E02003031', 'E00074083', null, 'XX1 0XX', 'E12000009', 'ACTIONABLE',
        'Windleybury', 'HH_LF3R3AE', '123456789012345');



INSERT INTO casev2.uac_qid_link (id, qid, uac, unique_number, caze_case_ref)
VALUES ('3ee075b6-5e94-4c91-9b34-a64f22228b91', '0120000000000100', 'qhb6c2vx7mdf6m9l', 1, 10000000);

INSERT INTO casev2.uac_qid_link (id, qid, uac, unique_number, caze_case_ref)
VALUES ('48bcbef4-3f9e-42fb-b403-63766d47189f', '0120000000000200', 'ngcpbr3qxlsqxh4c', 2, 10000001);



INSERT INTO casev2.event (id, event_date, event_description, uac_qid_link_id)
VALUES ('401ac27a-5896-462e-a935-54d871c13b17', '2019-05-01 13:17:10.011000', 'Case created',
        '3ee075b6-5e94-4c91-9b34-a64f22228b91');

INSERT INTO casev2.event (id, event_date, event_description, uac_qid_link_id)
VALUES ('cc23f012-2bfa-4067-9aef-9e97d9979882', '2019-05-01 13:17:20.022000', 'Case created',
        '48bcbef4-3f9e-42fb-b403-63766d47189f');
