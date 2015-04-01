LOCK TABLES `design_element` WRITE;
/*!40000 ALTER TABLE `design_element` DISABLE KEYS */;
INSERT INTO `design_element` VALUES
(87,'Crate 1',2,NULL,NULL,58,NULL,'',NULL,296),
(88,'IOCLIC2',2,NULL,NULL,107,NULL,'',NULL,297),
(89,'ADC1',2,NULL,NULL,89,NULL,'',NULL,298),
(90,'ADC2',2,NULL,NULL,89,NULL,'',NULL,299),
(91,'ADC3',2,NULL,NULL,89,NULL,'',NULL,300),
(92,'DAC1',2,NULL,NULL,90,NULL,'',NULL,301),
(93,'CM1',2,NULL,NULL,139,NULL,'',NULL,302),
(94,'CM2',2,NULL,NULL,139,NULL,'',NULL,303),
(95,'CM3',2,NULL,NULL,139,NULL,'',NULL,304),
(96,'Reflective Memory 1',5,NULL,NULL,158,NULL,'Reflective Memory',5.00,305),
(97,'CPU1',5,NULL,NULL,159,NULL,'CPU',2.00,306),
(98,'Chassis 1',5,NULL,NULL,156,138,'',1.00,307),
(99,'EVR1',5,NULL,NULL,157,NULL,'Event Receiver',3.00,308),
(100,'ctlsdaqdev2',6,NULL,NULL,108,NULL,'',NULL,309),
(101,'LLRF4 Controller #1',6,NULL,NULL,88,NULL,'',NULL,310),
(102,'ctlsdaqdev1',7,NULL,NULL,108,NULL,'',NULL,311),
(103,'SPEC Board',7,NULL,NULL,87,NULL,'PCIe board from CERN',NULL,312),
(104,'ctlsdaqsrv1',4,NULL,NULL,108,NULL,'workstation for DAQ services/storage',NULL,313),
(105,'Ctlr1',16,NULL,NULL,38,NULL,'',NULL,314),
(106,'Cable1',16,NULL,NULL,39,NULL,'',NULL,315),
(107,'Pump1',16,NULL,NULL,36,NULL,'',NULL,316),
(108,'BPM1',16,NULL,NULL,28,NULL,'',NULL,317),
(109,'BPM2',16,NULL,NULL,28,NULL,'',NULL,318),
(110,'BPM3',16,NULL,NULL,28,NULL,'',NULL,319),
(111,'Chamber6',16,NULL,NULL,8,NULL,'',NULL,320),
(112,'Chamber4',16,NULL,NULL,6,NULL,'',NULL,321),
(113,'Chamber5',16,NULL,NULL,7,NULL,'',NULL,322),
(114,'Chamber8',16,NULL,NULL,10,NULL,'',NULL,323),
(115,'Chamber7',16,NULL,NULL,9,NULL,'',NULL,324),
(116,'Gauge1',16,NULL,NULL,35,NULL,'',NULL,325),
(117,'Cable2',16,NULL,NULL,42,NULL,'',NULL,326),
(118,'Ctlr2',16,NULL,NULL,40,NULL,'',NULL,327),
(119,'Cable3',16,NULL,NULL,41,NULL,'',NULL,328),
(129,'B:Q2',20,NULL,NULL,171,NULL,'',1.00,338),
(130,'B:Q1',20,NULL,NULL,169,NULL,'',2.00,339),
(131,'A:Q6',22,NULL,NULL,171,NULL,'',7.00,340),
(132,'A:Q3',22,NULL,NULL,172,NULL,'',1.00,341),
(133,'A:Q5',22,NULL,NULL,172,NULL,'',5.00,342),
(134,'A:Q4',22,NULL,NULL,171,NULL,'',3.00,343),
(135,'A:S1',22,NULL,NULL,173,NULL,'',2.00,344),
(136,'A:S2',22,NULL,NULL,174,NULL,'',4.00,345),
(137,'A:S3',22,NULL,NULL,173,NULL,'',6.00,346),
(138,'B:Q5',23,NULL,NULL,172,NULL,'',3.00,347),
(139,'B:Q6',23,NULL,NULL,171,NULL,'',1.00,348),
(140,'B:Q4',23,NULL,NULL,171,NULL,'',5.00,349),
(141,'B:S2',23,NULL,NULL,174,NULL,'',4.00,350),
(142,'B:Q3',23,NULL,NULL,172,NULL,'',7.00,351),
(143,'B:S1',23,NULL,NULL,173,NULL,'',6.00,352),
(144,'B:S3',23,NULL,NULL,173,NULL,'',2.00,353),
(145,'A:Q8',24,NULL,NULL,171,NULL,'',3.00,354),
(146,'B:Q8',24,NULL,NULL,171,NULL,'',5.00,355),
(147,'A:Q7',24,NULL,NULL,169,NULL,'',1.00,356),
(148,'B:Q7',24,NULL,NULL,169,NULL,'',7.00,357),
(149,'A:M3',24,NULL,NULL,175,NULL,'',2.00,358),
(150,'B:M3',24,NULL,NULL,175,NULL,'',6.00,359),
(151,'A:M4',24,NULL,NULL,175,NULL,'',4.00,360),
(152,'S01A:P1',28,NULL,NULL,179,NULL,'',1.00,361),
(153,'S01A:Q1',28,NULL,NULL,176,NULL,'',2.00,362),
(154,'S01A:Q2',28,NULL,NULL,176,NULL,'',3.00,363),
(155,'S01A:S1',28,NULL,NULL,177,NULL,'',4.00,364),
(156,'RTFB DAQ',4,NULL,5,NULL,NULL,'',1.00,365),
(157,'SR RF DAQ',4,NULL,6,NULL,NULL,'',2.00,366),
(158,'DSC DAQ',4,NULL,7,NULL,NULL,'',3.00,367),
(159,'DMM Water Handling System',13,NULL,14,NULL,NULL,'',1.00,368),
(160,'DMM Magnets',13,NULL,15,NULL,NULL,'',2.00,369),
(161,'DMM Vacuum Components',13,NULL,16,NULL,NULL,'',3.00,370),
(162,'DMM Supports',13,NULL,17,NULL,NULL,'',4.00,371),
(164,'Girder 2',21,NULL,25,NULL,NULL,'A:M1',2.00,373),
(165,'Girder 4',21,NULL,25,NULL,NULL,'A:M2',4.00,374),
(166,'Girder 3',21,NULL,22,NULL,NULL,'A:Q3, A:S1, A:Q4, A:S2, A:Q5, A:S3, A:Q6',3.00,375),
(167,'Girder 8',21,NULL,25,NULL,NULL,'B:M1',8.00,376),
(168,'Girder 6',21,NULL,25,NULL,NULL,'B:M2',6.00,377),
(169,'Girder 9',21,NULL,20,NULL,NULL,'B:Q2, B:Q1',9.00,378),
(170,'Girder 7',21,NULL,22,NULL,NULL,'B:Q6, B:S3, B:Q5, B:S2, B:Q4, B:S1, B:Q3',7.00,379),
(171,'Girder 5',21,NULL,24,NULL,NULL,'A:Q7, A:M3, A:Q8, A:M4, B:Q8, B:M3, B:Q7',5.00,380),
(172,'Girder 1',21,NULL,19,NULL,NULL,'A:Q1, A:Q2',1.00,381),
(173,'Q6a',15,NULL,NULL,166,NULL,NULL,1.00,420),
(174,'Q6b',15,NULL,NULL,166,NULL,NULL,2.00,419),
(175,'Q6d',15,NULL,NULL,166,NULL,NULL,5.00,424),
(176,'S1a',15,NULL,NULL,167,NULL,NULL,4.00,422),
(177,'Q6c',15,NULL,NULL,166,NULL,NULL,3.00,423),
(179,'Base',17,NULL,NULL,168,NULL,NULL,NULL,425),
(180,'Plinth',17,NULL,NULL,187,NULL,'',NULL,426),
(183,'AQ1',29,NULL,NULL,176,NULL,'',2.00,431),
(184,'AQ2',29,NULL,NULL,176,NULL,'',1.00,432),
(185,'uTCA CPU',32,NULL,NULL,194,245,NULL,4.00,452),
(186,'uTCA Chassis',32,NULL,NULL,208,245,NULL,1.00,450),
(188,'uTCA MCH',32,NULL,NULL,193,245,NULL,3.00,449),
(189,'DSC Unit 1',31,NULL,32,NULL,NULL,NULL,1.00,455),
(190,'Brilliance+ Unit 1',31,NULL,NULL,197,NULL,NULL,NULL,454),
(191,'A:Q1',19,NULL,NULL,169,NULL,NULL,NULL,457),
(193,'rf bpm electronics #1',33,NULL,NULL,197,NULL,NULL,5.00,461),
(194,'rf bpm electronics #2',33,NULL,NULL,197,NULL,NULL,6.00,462),
(195,'rf bpm electronics #3',33,NULL,NULL,197,NULL,NULL,7.00,464),
(196,'rf bpm electronics #4',33,NULL,NULL,197,NULL,NULL,8.00,463),
(198,'Upstream partial DSC',33,NULL,NULL,104,NULL,NULL,9.00,467),
(199,'DSC-to-DSC fast link',33,NULL,NULL,201,NULL,NULL,4.00,472),
(200,'Double-sector controller IOC',33,NULL,NULL,199,NULL,NULL,3.00,471),
(201,'Orbit feedback algorithm',33,NULL,NULL,200,NULL,NULL,2.00,473),
(202,'Fast corrector setpoint link',33,NULL,NULL,NULL,NULL,NULL,NULL,474),
(203,'DSP/FPGA Card',32,NULL,NULL,196,245,NULL,2.00,477),
(204,'Double-sector controller',33,NULL,32,NULL,NULL,NULL,1.00,478),
(205,'VSM:A:GV1',34,NULL,NULL,1,398,NULL,NULL,496),
(206,'VSM:B:GV1',34,NULL,NULL,2,398,NULL,NULL,497),
(207,'VSM:A:IP1',34,NULL,NULL,210,398,NULL,NULL,501),
(208,'VSM:A:IP2',34,NULL,NULL,210,398,NULL,NULL,502),
(209,'VSM:B:IP2',34,NULL,NULL,210,398,NULL,NULL,503),
(210,'VSM:B:IP1',34,NULL,NULL,210,398,NULL,NULL,505),
(211,'VSM:A:IP3',34,NULL,NULL,210,398,NULL,NULL,504),
(212,'VSM:TC1',34,NULL,NULL,212,398,NULL,NULL,522),
(213,'VSM:A:IPCTL2',34,NULL,NULL,38,398,NULL,NULL,525),
(214,'VSM:A:IPCTL1',34,NULL,NULL,38,398,NULL,NULL,524),
(215,'VSM:B:IPCTL1',34,NULL,NULL,38,398,NULL,NULL,523),
(216,'VSM:B:RGA1',34,NULL,NULL,37,398,NULL,NULL,526),
(217,'VSM:NEGCTL1',34,NULL,NULL,211,398,NULL,NULL,527),
(218,'VSM:B:VGCTL1',34,NULL,NULL,40,398,NULL,NULL,529),
(219,'VSM:A:VGCTL1',34,NULL,NULL,40,398,NULL,NULL,528);
/*!40000 ALTER TABLE `design_element` ENABLE KEYS */;
UNLOCK TABLES;
