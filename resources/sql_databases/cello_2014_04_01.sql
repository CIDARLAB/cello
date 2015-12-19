-- MySQL dump 10.13  Distrib 5.6.10, for osx10.7 (x86_64)
--
-- Host: localhost    Database: cello_2014_02_04
-- ------------------------------------------------------
-- Server version	5.6.10

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Equation`
--

DROP TABLE IF EXISTS `Equation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Equation` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ModelID` int(10) unsigned NOT NULL,
  `EquationTypeID` int(10) unsigned NOT NULL,
  `Formula` varchar(500) NOT NULL COMMENT 'in Latex format',
  PRIMARY KEY (`ID`),
  KEY `FK_Equation_EquationSetID_idx` (`ModelID`),
  KEY `FK_Equation_EquationType_idx` (`EquationTypeID`),
  CONSTRAINT `FK_Equation_EquationType` FOREIGN KEY (`EquationTypeID`) REFERENCES `EquationType` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_Equation_ModelID` FOREIGN KEY (`ModelID`) REFERENCES `Model` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Equation`
--

LOCK TABLES `Equation` WRITE;
/*!40000 ALTER TABLE `Equation` DISABLE KEYS */;
INSERT INTO `Equation` VALUES (1,1,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(2,2,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(3,3,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(4,4,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(5,5,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(6,6,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(7,7,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(8,8,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(9,9,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(10,10,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(11,11,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(12,12,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(13,13,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(14,14,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(15,15,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(16,16,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(17,17,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(18,18,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(19,19,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(20,20,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(21,21,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(22,22,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(23,23,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(24,24,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(25,25,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(26,26,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(27,27,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(28,28,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(29,29,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(30,30,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(31,31,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(32,32,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(33,33,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(34,34,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(35,35,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(36,36,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(37,37,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(38,38,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)'),(39,39,1,'pmin+(pmax-pmin)/(1.0+(pTac/Kd)^n)');
/*!40000 ALTER TABLE `Equation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `EquationType`
--

DROP TABLE IF EXISTS `EquationType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EquationType` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Type` varchar(20) NOT NULL COMMENT 'Xfer_associated;\\nXfer_function;\\nFSP_production; \\nFSP_degradation;  ',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `EquationType`
--

LOCK TABLES `EquationType` WRITE;
/*!40000 ALTER TABLE `EquationType` DISABLE KEYS */;
INSERT INTO `EquationType` VALUES (1,'Xfer_production'),(2,'Xfer_associated'),(3,'FSP_production'),(4,'FSP_degradation');
/*!40000 ALTER TABLE `EquationType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Gate`
--

DROP TABLE IF EXISTS `Gate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Gate` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) DEFAULT NULL,
  `PartID` int(10) unsigned NOT NULL,
  `GateTypeID` int(10) unsigned NOT NULL,
  `Input1` int(10) unsigned NOT NULL,
  `Input2` int(10) unsigned DEFAULT NULL,
  `Output` int(10) unsigned NOT NULL,
  `Source` text,
  PRIMARY KEY (`ID`),
  KEY `FK_GateType_idx` (`GateTypeID`),
  KEY `FK_Input1_idx` (`Input1`),
  KEY `FK_Output_idx` (`Output`),
  KEY `FK_Gate_PartID_idx` (`PartID`),
  CONSTRAINT `FK_Gate_GateType` FOREIGN KEY (`GateTypeID`) REFERENCES `GateType` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_Gate_Input1` FOREIGN KEY (`Input1`) REFERENCES `Part` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_Gate_Output` FOREIGN KEY (`Output`) REFERENCES `Part` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_Gate_PartID` FOREIGN KEY (`PartID`) REFERENCES `Part` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Gate`
--

LOCK TABLES `Gate` WRITE;
/*!40000 ALTER TABLE `Gate` DISABLE KEYS */;
INSERT INTO `Gate` VALUES (1,'NOT_PhlF',20,1,8,0,23,''),(2,'NOT_McbR',25,1,8,0,28,''),(3,'NOT_LmrA',30,1,8,0,33,''),(4,'NOT_AmeR',35,1,8,0,38,''),(5,'NOT_AmtR',40,1,8,0,43,''),(6,'NOT_BM3R1',45,1,8,0,48,''),(7,'NOT_BetI',50,1,8,0,53,''),(8,'NOT_ButR',55,1,8,0,58,''),(9,'NOT_HapR',60,1,8,0,63,''),(10,'NOT_IcaR',65,1,8,0,68,''),(11,'NOT_Orf2',70,1,8,0,73,''),(12,'NOT_PsrA',75,1,8,0,78,''),(13,'NOT_QacR',80,1,8,0,83,''),(14,'NOT_ScbR',85,1,8,0,88,''),(15,'NOT_SmcR',90,1,8,0,93,''),(16,'NOT_SrpR',95,1,8,0,98,''),(17,'NOT_TarA',100,1,8,0,103,''),(18,'NOT_TetR',105,1,8,0,108,''),(19,'INDUCER_pTac',110,6,3,0,8,''),(20,'INDUCER_pLuxStar',111,6,3,0,10,''),(21,'INDUCER_pBAD',112,6,3,0,11,''),(22,'INDUCER_pTet',113,6,3,0,13,''),(23,'REPORTER_YFP',114,7,8,0,17,''),(24,'NOT_LtsvJ-PhlF',176,1,8,0,23,''),(25,'NOT_PlmJ-TarA',178,1,8,0,103,''),(26,'NOT_SarJ-BM3R1',177,1,8,0,48,''),(27,'NOT_SccJ-SrpR',179,1,8,0,98,''),(28,'NOT_VtmoJ-HlyIIR',180,1,8,0,188,''),(29,'INDUCER_pLux',190,6,3,0,9,''),(30,'NOT_PlmJ-AmtR',192,1,8,0,43,''),(31,'NOT_rbs1-PhlF',193,1,8,0,98,''),(32,'NOT_rbs1-SrpR',194,1,8,0,23,''),(33,'NOT_rbs1-BM3R1',195,1,8,0,48,''),(34,'NOT_rbs2-QacR',196,1,8,0,43,''),(35,'NOT_rbs1-AmtR',197,1,8,0,188,''),(36,'NOT_rbs2-HlyIIR',198,1,8,0,103,''),(37,'NOT_rbs1-IcaRA',199,1,8,0,73,''),(38,'NOT_rbs2-BetI',200,1,8,0,33,''),(39,'NOT_rbs2-LitR',201,1,8,0,33,''),(40,'NOT_rbs0-PhlF',202,1,8,0,23,''),(41,'NOT_rbs0-SrpR',203,1,8,0,98,''),(42,'NOT_rbs0-BM3R1',204,1,8,0,48,''),(43,'NOT_rbs0-QacR',205,1,8,0,83,''),(44,'NOT_rbs2-SrpR',208,1,8,0,98,NULL),(45,'NOT_rbs3-SrpR',209,1,8,0,98,NULL);
/*!40000 ALTER TABLE `Gate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GType`
--

DROP TABLE IF EXISTS `GateType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GateType` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Type` varchar(20) NOT NULL COMMENT 'AND, NOR, etc,',
  `NInput` tinyint(1) unsigned NOT NULL COMMENT 'number of input',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GType`
--

LOCK TABLES `GateType` WRITE;
/*!40000 ALTER TABLE `GType` DISABLE KEYS */;
INSERT INTO `GateType` VALUES (1,'NOT',1),(2,'OR',2),(3,'NOR',2),(4,'AND',2),(5,'NAND',2),(6,'INDUCER',1),(7,'REPORTER',1);
/*!40000 ALTER TABLE `GType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ResponseFunction`
--

DROP TABLE IF EXISTS `Model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Model` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PartID` int(10) unsigned DEFAULT NULL,
  `Source` text COMMENT 'reference to paper, equation number',
  PRIMARY KEY (`ID`),
  KEY `FK_Model_PartID_idx` (`PartID`),
  CONSTRAINT `FK_Model_PartID` FOREIGN KEY (`PartID`) REFERENCES `Part` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ResponseFunction`
--

LOCK TABLES `Model` WRITE;
/*!40000 ALTER TABLE `ResponseFunction` DISABLE KEYS */;
INSERT INTO `Model` VALUES (1,20,'in-house'),(2,25,'in-house'),(3,30,'in-house'),(4,35,'in-house'),(5,40,'in-house'),(6,45,'in-house'),(7,50,'in-house'),(8,55,'in-house'),(9,60,'in-house'),(10,65,'in-house'),(11,70,'in-house'),(12,75,'in-house'),(13,80,'in-house'),(14,85,'in-house'),(15,90,'in-house'),(16,95,'in-house'),(17,100,'in-house'),(18,105,'in-house'),(19,176,'in-house'),(20,177,'in-house'),(21,178,'in-house'),(22,179,'in-house'),(23,180,'in-house'),(24,192,'in-house'),(25,193,'in-house'),(26,194,'in-house'),(27,195,'in-house'),(28,196,'in-house'),(29,197,'in-house'),(30,198,'in-house'),(31,199,'in-house'),(32,200,'in-house'),(33,201,'in-house'),(34,202,'in-house'),(35,203,'in-house'),(36,204,'in-house'),(37,205,'in-house'),(38,208,'in-house'),(39,209,'in-house\n');
/*!40000 ALTER TABLE `ResponseFunction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Param`
--

DROP TABLE IF EXISTS `Param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Param` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ModelID` int(10) unsigned NOT NULL,
  `Name` varchar(20) NOT NULL COMMENT 'reference to paper',
  `Value` float DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_EquationID_idx` (`ModelID`),
  CONSTRAINT `FK_Param_ModelID` FOREIGN KEY (`ModelID`) REFERENCES `Model` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Param`
--

LOCK TABLES `Param` WRITE;
/*!40000 ALTER TABLE `Param` DISABLE KEYS */;
INSERT INTO `Param` VALUES (1,1,'pmin',0.083),(2,1,'pmax',15.973),(3,1,'Kd',0.361),(4,1,'n',4.55),(5,2,'pmin',1.144),(6,2,'pmax',15.902),(7,2,'Kd',0.409),(8,2,'n',1.586),(9,3,'pmin',1.147),(10,3,'pmax',70.16),(11,3,'Kd',1.243),(12,3,'n',3.059),(13,4,'pmin',0),(14,4,'pmax',0),(15,4,'Kd',0),(16,4,'n',0),(17,5,'pmin',0.324),(18,5,'pmax',9.215),(19,5,'Kd',0.242),(20,5,'n',1.791),(21,6,'pmin',0.093),(22,6,'pmax',2.562),(23,6,'Kd',0.552),(24,6,'n',4.481),(25,7,'pmin',0.377),(26,7,'pmax',13.321),(27,7,'Kd',0.207),(28,7,'n',2.357),(29,8,'pmin',1.849),(30,8,'pmax',12.161),(31,8,'Kd',1.013),(32,8,'n',2.012),(33,9,'pmin',1.515),(34,9,'pmax',10.204),(35,9,'Kd',0.107),(36,9,'n',1.339),(37,10,'pmin',0.366),(38,10,'pmax',12.571),(39,10,'Kd',0.416),(40,10,'n',1.752),(41,11,'pmin',0.207),(42,11,'pmax',14.171),(43,11,'Kd',0.375),(44,11,'n',6.129),(45,12,'pmin',0.46),(46,12,'pmax',19.908),(47,12,'Kd',0.414),(48,12,'n',1.985),(49,13,'pmin',0.659),(50,13,'pmax',20.972),(51,13,'Kd',0.481),(52,13,'n',1.742),(53,14,'pmin',0.616),(54,14,'pmax',5.222),(55,14,'Kd',0.143),(56,14,'n',2.461),(57,15,'pmin',2.347),(58,15,'pmax',12.554),(59,15,'Kd',0.095),(60,15,'n',1.811),(61,16,'pmin',0.122),(62,16,'pmax',25.232),(63,16,'Kd',0.311),(64,16,'n',3.276),(65,17,'pmin',0.223),(66,17,'pmax',12.855),(67,17,'Kd',0.091),(68,17,'n',1.828),(69,18,'pmin',0.178),(70,18,'pmax',24.336),(71,18,'Kd',0.095),(72,18,'n',2.665),(73,19,'pmin',0.0188736),(74,19,'pmax',20.8746),(75,19,'Kd',0.0806827),(76,19,'n',3.85467),(77,20,'pmin',0.03187),(78,20,'pmax',1.77633),(79,20,'Kd',0.103043),(80,20,'n',3.55014),(81,21,'pmin',0.105442),(82,21,'pmax',2.76629),(83,21,'Kd',0.799086),(84,21,'n',1.52971),(85,22,'pmin',0.0178353),(86,22,'pmax',30.413),(87,22,'Kd',0.197451),(88,22,'n',2.95187),(89,23,'pmin',0.0926914),(90,23,'pmax',17.3191),(91,23,'Kd',4.92884),(92,23,'n',3.17637),(93,24,'pmin',0.0498889),(94,24,'pmax',17.7122),(95,24,'Kd',0.457648),(96,24,'n',2.48188),(98,25,'pmin',0.073805),(99,25,'pmax',17.2204),(100,25,'Kd',0.560868),(101,25,'n',3.87694),(102,26,'pmin',0.011192),(103,26,'pmax',5.53837),(104,26,'Kd',0.043338),(105,26,'n',2.92615),(106,27,'pmin',0.019056),(107,27,'pmax',2.16849),(108,27,'Kd',0.627955),(109,27,'n',2.94565),(110,28,'pmin',0.052934),(111,28,'pmax',10.3018),(112,28,'Kd',0.229763),(113,28,'n',2.68779),(114,29,'pmin',0.316394),(115,29,'pmax',13.187),(116,29,'Kd',0.169953),(117,29,'n',1.31913),(118,30,'pmin',4.70236),(119,30,'pmax',12.1166),(120,30,'Kd',4.55554),(121,30,'n',6.30492),(122,31,'pmin',0.352692),(123,31,'pmax',9.21985),(124,31,'Kd',0.404457),(125,31,'n',1.43724),(126,32,'pmin',1.7562),(127,32,'pmax',17.4368),(128,32,'Kd',2.54833),(129,32,'n',2.63302),(130,33,'pmin',0.263379),(131,33,'pmax',10.2008),(132,33,'Kd',0.138481),(133,33,'n',1.54255),(134,34,'pmin',0.061101),(135,34,'pmax',16.2885),(136,34,'Kd',0.115825),(137,34,'n',4.03135),(138,35,'pmin',0.030248),(139,35,'pmax',8.8217),(140,35,'Kd',0.415826),(141,35,'n',2.80988),(142,36,'pmin',0.01804),(143,36,'pmax',2.05597),(144,36,'Kd',0.165888),(145,36,'n',3.37705),(146,37,'pmin',0.139141),(147,37,'pmax',12.0415),(148,37,'Kd',0.871797),(149,37,'n',2.41975),(150,38,'pmin',0.013618),(151,38,'pmax',8.72034),(152,38,'Kd',0.157783),(153,38,'n',2.64759),(154,39,'pmin',0.017023),(155,39,'pmax',8.96637),(156,39,'Kd',0.267569),(157,39,'n',2.79516);
/*!40000 ALTER TABLE `Param` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Part`
--

DROP TABLE IF EXISTS `Part`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Part` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'PartTable',
  `Name` varchar(100) DEFAULT NULL COMMENT 'e.g. sicA, invF, etc',
  `PartTypeID` int(10) unsigned NOT NULL COMMENT 'gene, promoter',
  `isBasic` tinyint(1) DEFAULT NULL,
  `Direction` varchar(1) DEFAULT NULL COMMENT '+ for 5\\'' to 3\\''\\n- for 3\\'' to 5\\'', on the complement strand',
  `ApeColor` varchar(7) DEFAULT NULL,
  `Source` text COMMENT 'references',
  `DNASeq` text COMMENT 'nucleotide sequences',
  PRIMARY KEY (`ID`),
  KEY `FK_PartTypeID_idx` (`PartTypeID`),
  CONSTRAINT `FK_Part_PartTypeID` FOREIGN KEY (`PartTypeID`) REFERENCES `PartType` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Part`
--

LOCK TABLES `Part` WRITE;
/*!40000 ALTER TABLE `Part` DISABLE KEYS */;
INSERT INTO `Part` VALUES (1,'AmpR',20,1,'+','#0000ff','/Users/peng/work/cello/ape/PhlF.ape','atgagcacttttaaagttctgctatgtggcgcggtattatcccgtgttgacgccgggcaagagcaactcggtcgccgcatacactattctcagaatgacttggttgagtactcaccagtcacagaaaagcatcttacggatggcatgacagtaagagaattatgcagtgctgccataaccatgagtgataacactgcggccaacttacttctgacaacgatcggaggaccgaaggagctaaccgcttttttgcacaacatgggggatcatgtaactcgccttgatcgttgggaaccggagctgaatgaagccataccaaacgacgagcgtgacaccacgatgcctgcagcaatggcaacaacgttgcgcaaactattaactggcgaactacttactctagcttcccggcaacaattaatagactggatggaggcggataaagttgcaggaccacttctgcgctcggcccttccggctggctggtttattgctgataaatctggagccggtgagcgtgggtctcgcggtatcattgcagcactggggccagatggtaagccctcccgtatcgtagttatctacacgacggggagtcaggcaactatggatgaacgaaatagacagatcgctgagataggtgcctcactgattaagcattgg'),(2,'p15A',15,1,'+','pink','Alec circuit_backbone.ape','gacctcagcgctagcggagtgtatactggcttactatgttggcactgatgagggtgtcagtgaagtgcttcatgtggcaggagaaaaaaggctgcaccggtgcgtcagcagaatatgtgatacaggatatattccgcttcctcgctcactgactcgctacgctcggtcgttcgactgcggcgagcggaaatggcttacgaacggggcggagatttcctggaagatgccaggaagatacttaacagggaagtgagagggccgcggcaaagccgtttttccataggctccgcccccctgacaagcatcacgaaatctgacgctcaaatcagtggtggcgaaacccgacaggactataaagataccaggcgtttcccctggcggctccctcgtgcgctctcctgttcctgcctttcggtttaccggtgtcattccgctgttatggccgcgtttgtctcattccacgcctgacactcagttccgggtaggcagttcgctccaagctggactgtatgcacgaaccccccgttcagtccgaccgctgcgccttatccggtaactatcgtcttgagtccaacccggaaagacatgcaaaagcaccactggcagcagccactggtaattgatttagaggagttagtcttgaagtcatgcgccggttaaggctaaactgaaaggacaagttttggtgactgcgctcctccaagccagttacctcggttcaaagagttggtagctcagagaaccttcgaaaaaccgccctgcaaggcggttttttcgttttcagagcaagagattacgcgcagaccaaaacgatctcaagaagatcatcttattaa'),(3,'pCon+RBS',17,1,'-','#00ff00','/Users/peng/work/cello/ape/PhlF.ape','ATTCACCACCCTGAATTGACTCTCTTCCGGGCGCTATCATGCCATACCGCGAAAGGTTTTGCGCCATTCGATGGCGCGCCGC'),(4,'LacI_CDS',18,1,'-','#87cefa','/Users/peng/work/cello/ape/PhlF.ape','TCACTGCCCGCTTTCCAGTCGGGAAACCTGTCGTGCCAGCTGCATTAATGAATCGGCCAACGCGCGGGGAGAGGCGGTTTGCGTATTGGGCGCCAGGGTGGTTTTTCTTTTCACCAGTGAGACTGGCAACAGCTGATTGCCCTTCACCGCCTGGCCCTGAGAGAGTTGCAGCAAGCGGTCCACGCTGGTTTGCCCCAGCAGGCGAAAATCCTGTTTGATGGTGGTTAACGGCGGGATATAACATGAGCTATCTTCGGTATCGTCGTATCCCACTACCGAGATATCCGCACCAACGCGCAGCCCGGACTCGGTAATGGCGCGCATTGCGCCCAGCGCCATCTGATCGTTGGCAACCAGCATCGCAGTGGGAACGATGCCCTCATTCAGCATTTGCATGGTTTGTTGAAAACCGGACATGGCACTCCAGTCGCCTTCCCGTTCCGCTATCGGCTGAATTTGATTGCGAGTGAGATATTTATGCCAGCCAGCCAGACGCAGACGCGCCGAGACAGAACTTAATGGGCCCGCTAACAGCGCGATTTGCTGGTGACCCAATGCGACCAGATGCTCCACGCCCAGTCGCGTACCGTCCTCATGGGAGAAAATAATACTGTTGATGGGTGTCTGGTCAGAGACATCAAGAAATAACGCCGGAACATTAGTGCAGGCAGCTTCCACAGCAATGGCATCCTGGTCATCCAGCGGATAGTTAATGATCAGCCCACTGACGCGTTGCGCGAGAAGATTGTGCACCGCCGCTTTACAGGCTTCGACGCCGCTTCGTTCTACCATCGACACCACCACGCTGGCACCCAGTTGATCGGCGCGAGATTTAATCGCCGCGACAATTTGCGACGGCGCGTGCAGGGCCAGACTGGAGGTGGCAACGCCAATCAGCAACGACTGTTTGCCCGCCAGTTGTTGTGCCACGCGGTTGGGAATGTAATTCAGCTCCGCCATCGCCGCTTCCACTTTTTCCCGCGTTTTCGCAGAAACGTGGCTGGCCTGGTTCACCACGCGGGAAACGGTCTGATAAGAGACACCGGCATACTCTGCGACATCGTATAACGTTACTGGTTTCAT'),(5,'LuxR_CDS',18,1,'-','#87cefa','/Users/peng/work/cello/ape/PhlF.ape','ttaatttttaaagtatgggcaatcaattgctcctgttaaaattgctttagaaatactttggcagcggtttgttgtattgagtttcatttgcgcattggttaaatggaaagtgacagtacgctcactgcaacctaatatttttgaaatatcccaagagctttttccttcgcatgcccacgctaaacattctttttctcttttggttaaatcgttgtttgatttattatttgctatatttatttttcgataattatcaactagagaaggaacaattaatggtatgttcatacacgcatgtaaaaataaactatctatatagttgtctttttctgaatgtgcaaaactaagcattccgaagccattgttagccgtatgaatagggaaactaaacccagtgataagacctgatgttttcgcttctttaattacatttggagattttttatttacagcattgttttcaaatatattccaattaattggtgaatgattggagttagaataatctactataggatcatattttattaaattagcgtcatcataatattgcctccattttttagggtaattatctaggattgaaatatcagatttaaccatagaatgaggataaatgatcgcgagtaaataatattcacaatgtaccattttagtcatatcagataagcattgattaatatcattattgcttctacaagctttaattttattaattattctgtatgtgtcgtcggcatttatgtttttCAT'),(6,'AraC_CDS',18,1,'+','#87cefa','Table S4 Moon et al Nature 2012','atggctgaagcgcaaaatgatcccctgctgccgggatactcgtttaatgcccatctggtggcgggtttaacgccgattgaggccaacggttatctcgatttttttatcgaccgaccgctgggaatgaaaggttatattctcaatctcaccattcgcggtcagggggtggtgaaaaatcagggacgagaatttgtttgccgaccgggtgatattttgctgttcccgccaggagagattcatcactacggtcgtcatccggaggctcgcgaatggtatcaccagtgggtttactttcgtccgcgcgcctactggcatgaatggcttaactggccgtcaatatttgccaatacggggttctttcgcccggatgaagcgcaccagccgcatttcagcgacctgtttgggcaaatcattaacgccgggcaaggggaagggcgctattcggagctgctggcgataaatctgcttgagcaattgttactgcggcgcatggaagcgattaacgagtcgctccatccaccgatggataatcgggtacgcgaggcttgtcagtacatcagcgatcacctggcagacagcaattttgatatcgccagcgtcgcacagcatgtttgcttgtcgccgtcgcgtctgtcacatcttttccgccagcagttagggattagcgtcttaagctggcgcgaggaccaacgtatcagccaggcgaagctgcttttgagcaccacccggatgcctatcgccaccgtcggtcgcaatgttggttttgacgatcaactctatttctcgcgggtatttaaaaaatgcaccggggccagcccgagcgagttccgtgccggttgtgaagaaaaagtgaatgatgtagccgtcaagttgtcataa'),(7,'TetR_CDS',18,1,'+','#0000ff','/Users/peng/work/cello/ape/TetR.ape','ATGtccagattagataaaagtaaagtgattaacagcgcattagagctgcttaatgaggtcggaatcgaaggtttaacaacccgtaaactcgcccagaagctaggtgtagagcagcctacattgtattggcatgtaaaaaataagcgggctttgctcgacgccttagccattgagatgttagataggcaccatactcacttttgccctttagaaggggaaagctggcaagattttttacgtaataacgctaaaagttttagatgtgctttactaagtcatcgcgatggagcaaaagtacatttaggtacacggcctacagaaaaacagtatgaaactctcgaaaatcaattagcctttttatgccaacaaggtttttcactagagaatgcattatatgcactcagcgctgtggggcattttactttaggttgcgtattggaagatcaagagcatcaagtcgctaaagaagaaagggaaacacctactactgatagtatgccgccattattacgacaagctatcgaattatttgatcaccaaggtgcagagccagccttcttattcggccttgaattgatcatatgcggattagaaaaacaacttaaatgtgaaagtgggtcctaa'),(8,'pTac',19,1,'+','#00ff00','/Users/peng/work/cello/ape/PhlF.ape','tgttgacaattaatcatcggctcgtataatgtgtggaattgtgagcgctcacaatt'),(9,'pLux',19,1,'+','#00ff00','Table S4 Moon et al Nature 2012','acctgtaggatcgtacaggtttacgcaagaaaatggtttgttatagtcgaataaa'),(10,'pLuxStar',19,1,'+','#00ff00','Table S4 Moon et al Nature 2012','acctgtaggatcgtacaggtttacgcaagaaaatggtttgttactttcgaataaa'),(11,'pBAD',19,1,'+','#00ff00','Table S4 Moon et al Nature 2012','agaaaccaattgtccatattgcatcagacattgccgtcactgcgtcttttactggctcttctcgctaaccaaaccggtaaccccgcttattaaaagcattctgtaacaaagcgggaccaaagccatgacaaaaacgcgtaacaaaagtgtctataatcacggcagaaaagtccacattgattatttgcacggcgtcacactttgctatgccatagcatttttatccataagattagcggatcctacctg'),(12,'pTet',19,1,'+','#00ff00','Table S4 Moon et al Nature 2012','tttttccctatcagtgatagagattgacatccctatcagtgatagagatactgagcacctcg'),(13,'pTetStar',19,1,'+','#00ff00','Table S4 Moon et al Nature 2012','ttttcagcaggacgcactgacctccctatcagtgatagagattgacatccctatcagtgatagagatactgagcacatat'),(14,'B0034+SCARS',21,1,'+','#ff8040','/Users/peng/work/cello/ape/PhlF.ape','tactagagaaagaggagaaatactag'),(15,'GFP',7,1,'+','#90ee90','Table S4 Moon et al Nature 2012','atgagtaaaggagaagaacttttcactggagttgtcccaattcttgttgaattagatggtgatgttaatgggcacaaattttctgtcagtggagagggtgaaggtgatgcaacatacggaaaacttacccttaaatttatttgcactactggaaaactacctgttccatggccaacacttgtcactactttgacttatggtgttcaatgcttttcaagatacccagatcatatgaaacggcatgactttttcaagagtgccatgcccgaaggttatgtacaggaaagaactatatttttcaaagatgacgggaactataagacacgtgctgaagtcaagtttgaaggtgatacacttgttaatagaatcgagttaaaaggtattgattttaaagaagatggaaacattcttggacacaagttggaatacaactataactcacacaatgtatacatcatggcagacaaacaaaagaatggaatcaaagttaacttcaaaattagacacaacattgaagatggaagcgttcaactagcagaccattatcaacaaaatactccaattggcgatggccctgtccttttaccagacaaccattacctgtccacacaatctgccctttcgaaagatcccaacgaaaagagagaccacatggtccttcttgagtttgtaacagctgctgggattacacatggcatggatgaactatacaaaaggcctgcagcaaacgacgaaaactacgcttaa'),(16,'RFP',7,1,'+','#fa8072','Table S4 Moon et al Nature 2012','atggcttcctccgaagacgttatcaaagagttcatgcgtttcaaagttcgtatggaaggttccgttaacggtcacgagttcgaaatcgaaggtgaaggtgaaggtcgtccgtacgaaggtacgcagaccgctaaactgaaagttaccaaaggtggtccgctgccgttcgcttgggacatcctgtccccgcagttccagtacggttccaaagcttacgttaaacacccggctgacatcccggactacctgaaactgtccttcccggaaggtttcaaatgggaacgtgttatgaacttcgaagacggtggtgttgttaccgttacccaggactcctccctgcaagacggtgagttcatctacaaagttaaactgcgtggtactaacttcccgtccgacggtccggttatgcagaaaaaaaccatgggttgggaagcttccaccgaacgtatgtacccggaagacggtgctctgaaaggtgaaatcaaaatgcgtctgaaactgaaagacggtggtcactacgacgctgaagttaaaaccacctacatggctaaaaaaccggttcagctgccgggtgcttacaaaaccgacatcaaactggacatcacctcccacaacgaagactacaccatcgttgaacagtacgaacgtgctgaaggtcgtcactccaccggtgctgcagcaaacgacgaaaactacgcttaa'),(17,'YFP',7,1,'+','#ffd700','/Users/peng/work/cello/ape/PhlF.ape','atggtgagcaagggcgaggagctgttcaccggggtggtgcccatcctggtcgagctggacggcgacgtaaacggccacaagttcagcgtgtccggcgagggcgagggcgatgccacctacggcaagctgaccctgaagttcatctgcaccaccggcaagctgcccgtgccctggcccaccctcgtgaccaccttcggctacggcctgcaatgcttcgcccgctaccccgaccacatgaagctgcacgacttcttcaagtccgccatgcccgaaggctacgtccaggagcgcaccatcttcttcaaggacgacggcaactacaagacccgcgccgaggtgaagttcgagggcgacaccctggtgaaccgcatcgagctgaagggcatcgacttcaaggaggacggcaacatcctggggcacaagctggagtacaactacaacagccacaacgtctatatcatggccgacaagcagaagaacggcatcaaggtgaacttcaagatccgccacaacatcgaggacggcagcgtgcagctcgccgaccactaccagcagaacacccccatcggcgacggccccgtgctgctgcccgacaaccactacctgagctaccagtccgccctgagcaaagaccccaacgagaagcgcgatcacatggtcctgctggagttcgtgaccgccgccgggatcactctcggcatggacgagctgtacaagtaa'),(18,'terminator1',6,1,'+','#ff0000','/Users/peng/work/cello/ape/PhlF.ape','ccaggcatcaaataaaacgaaaggctcagtcgaaagactgggcctttcgttttatctgttgtttgtcggtgaacgctctctactagagtcacactggctcaccttcgggtgggcctttctgcgtttata'),(19,'terminator2',6,1,'+','#ff0000','/Users/peng/work/cello/ape/PhlF.ape','ccaggcatcaaataaaacgaaaggctcagtcgaaagactgggcctttcgttttatctgttgtttgtcggtgaacgctctc'),(20,'PhlF_NOT',10,0,'','','',''),(21,'PhlF_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/PhlF.ape','CTATGGACTATGTTTGAAAGGGAGAAATACTAG'),(22,'PhlF_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/PhlF.ape','ATGGCACGTACCCCGAGCCGTAGCAGCATTGGTAGCCTGCGTAGTCCGCATACCCATAAAGCAATTCTGACCAGCACCATTGAAATCCTGAAAGAATGTGGTTATAGCGGTCTGAGCATTGAAAGCGTTGCACGTCGTGCCGGTGCAAGCAAACCGACCATTTATCGTTGGTGGACCAATAAAGCAGCACTGATTGCCGAAGTGTATGAAAATGAAAGCGAACAGGTGCGTAAATTTCCGGATCTGGGTAGCTTTAAAGCCGATCTGGATTTTCTGCTGCGTAATCTGTGGAAAGTTTGGCGTGAAACCATTTGTGGTGAAGCATTTCGTTGTGTTATTGCAGAAGCACAGCTGGACCCTGCAACCCTGACCCAGCTGAAAGATCAGTTTATGGAACGTCGTCGTGAGATGCCGAAAAAACTGGTTGAAAATGCCATTAGCAATGGTGAACTGCCGAAAGATACCAATCGTGAACTGCTGCTGGATATGATTTTTGGTTTTTGTTGGTATCGCCTGCTGACCGAACAGCTGACCGTTGAACAGGATATTGAAGAATTTACCTTCCTGCTGATTAATGGTGTTTGTCCGGGTACACAGCGTTAA'),(23,'pPhlF',2,1,'+','#00ff00','/Users/peng/work/cello/ape/PhlF.ape','tctgattcgttaccaattgacATGATACGAAACGTACCGTATCGTTAAGGT'),(24,'PhlF_operator',9,1,'','','',''),(25,'McbR_NOT',10,0,'','','',''),(26,'McbR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/McbR.ape','CTATGGACTATGTAGGAGAAATACTAG'),(27,'McbR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/McbR.ape','ATGGCAGCAAGCGCAAGCGGTAAAAGCAAAACCAGTGCCGGTGCAAATCGTCGTCGTAATCGTCCGAGTCCGCGTCAGCGTCTGCTGGATAGCGCAACCAACCTGTTTACCACCGAAGGTATTCGTGTGATTGGTATTGATCGTATTCTGCGTGAAGCAGATGTTGCAAAAGCAAGCCTGTATAGCCTGTTTGGTAGCAAAGATGCACTGGTTATTGCCTATCTGGAAAATCTGGATCAGCTGTGGCGTGAAGCATGGCGTGAACGTACCGTTGGTATGAAAGATCCGGAAGATAAAATCATTGCGTTCTTTGATCAGTGCATCGAAGAAGAACCGGAAAAAGATTTTCGCGGTAGCCATTTTCAGAATGCAGCAAGCGAATATCCGCGTCCGGAAACCGATAGCGAAAAAGGTATTGTTGCAGCAGTTCTGGAACATCGTGAATGGTGTCATAAAACCCTGACCGATCTGCTGACCGAAAAAAATGGTTATCCGGGTACAACCCAGGCAAATCAGCTGCTGGTTTTTCTGGATGGTGGTCTGGCAGGTAGCCGTCTGGTTCATAACATTAGTCCGCTGGAAACCGCACGTGATCTGGCACGTCAGCTGCTGAGCGCACCGCCTGCAGATTATAGCATTTAA'),(28,'pMcbR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/McbR.ape','gattcgttaccaattgacaCTAGACCGGTCTGTCTAtataatgctagc'),(29,'McbR_operator',9,1,'','','',''),(30,'LmrA_NOT',10,0,'','','',''),(31,'LmrA_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/LmrA.ape','CTATGGACTATGTTTTCACACAGGAAAGGCCTCG'),(32,'LmrA_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/LmrA.ape','ATGAGCTATGGTGATAGCCGTGAAAAAATTCTGAGCGCAGCAACCCGTCTGTTTCAGCTGCAGGGTTATTATGGCACCGGTCTGAATCAGATTATCAAAGAAAGCGGTGCACCGAAAGGTAGCCTGTATTATCATTTTCCGGGTGGTAAAGAACAGCTGGCAATTGAAGCAGTGAACGAAATGAAAGAATATATCCGCCAGAAAATCGCCGATTGTATGGAAGCATGTACCGATCCGGCAGAAGGTATTCAGGCATTTCTGAAAGAACTGAGCTGTCAGTTTAGCTGTACCGAAGATATTGAAGGTCTGCCGGTTGGTCTGCTGGCAGCAGAAACCAGCCTGAAAAGCGAACCGCTGCGTGAAGCATGTCATGAAGCATATAAAGAATGGGCCAGCGTGTATGAAGAAAAACTGCGTCAGACCGGTTGTAGCGAAAGCCGTGCAAAAGAAGCAAGCACCGTTGTTAATGCAATGATTGAAGGTGGTATTCTGCTGAGCCTGACCGCAAAAAATAGCACACCGCTGCTGCATATTAGCAGCTGTATTCCGGATCTGCTGAAACGTTAA'),(33,'pLmrA',2,1,'+','#00ff00','/Users/peng/work/cello/ape/LmrA.ape','TCTGATTCGTTACCAATTGACAACTGGTGGTCGAATCAAGATAATAGACCAGTCACTATATTT'),(34,'LmrA_operator',9,1,'','','',''),(35,'AmeR_NOT',10,0,'','','',''),(36,'AmeR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/AmeR.ape','CTATGGACTATGTTTGAAAGAGGAGAAATACTAG'),(37,'AmeR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/AmeR.ape','ATGAACAAAACCATTGATCAGGTGCGTAAAGGTGATCGTAAAAGCGATCTGCCGGTTCGTCGTCGTCCGCGTCGTAGTGCCGAAGAAACCCGTCGTGATATTCTGGCAAAAGCCGAAGAACTGTTTCGTGAACGTGGTTTTAATGCAGTTGCCATTGCAGATATTGCAAGCGCACTGAATATGAGTCCGGCAAATGTGTTTAAACATTTTAGCAGCAAAAACGCACTGGTTGATGCAATTGGTTTTGGTCAGATTGGTGTTTTTGAACGTCAGATTTGTCCGCTGGATAAAAGCCATGCACCGCTGGATCGTCTGCGTCATCTGGCACGTAATCTGATGGAACAGCATCATCAGGATCTGAATAAAAACCCGTATGTGTTTGAAATGATCCTGATGACCGCCAAACAGGATATGAAATGTGGCGATTATTACAAAAGCGTGATTGCAAAACTGCTGGCCGAAATTATTCGTGATGGTGTTGAAGCAGGTCTGTATATTGCAACCGATATTCCGGTTCTGGCAGAAACCGTTCTGCATGCACTGACCAGCGTTATTCATCCGGTTCTGATTGCACAAGAAGATATTGGTAATCTGGCAACCCGTTGTGATCAGCTGGTTGATCTGATTGATGCAGGTCTGCGTAATCCGCTGGCAAAATAA'),(38,'pAmeR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/AmeR.ape','ctgcaaaccctatgctactccctcgagccgtcaattgtctgattAGTGACGCCATTGACAGGAGGTCACTtcctaggtataatgctagc'),(39,'AmeR_operator',9,1,'','','',''),(40,'AmtR_NOT',10,0,'','','',''),(41,'AmtR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/AmtR.ape','CTATGGACTATGTTTGAAAGAGAGAATACTAG'),(42,'AmtR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/AmtR.ape','ATGGCAGGCGCAGTTGGTCGTCCGCGTCGTAGTGCACCGCGTCGTGCAGGTAAAAATCCGCGTGAAGAAATTCTGGATGCAAGCGCAGAACTGTTTACCCGTCAGGGTTTTGCAACCACCAGTACCCATCAGATTGCAGATGCAGTTGGTATTCGTCAGGCAAGCCTGTATTATCATTTTCCGAGCAAAACCGAAATCTTTCTGACCCTGCTGAAAAGCACCGTTGAACCGAGCACCGTTCTGGCAGAAGATCTGAGCACCCTGGATGCAGGTCCGGAAATGCGTCTGTGGGCAATTGTTGCAAGCGAAGTTCGTCTGCTGCTGAGCACCAAATGGAATGTTGGTCGTCTGTATCAGCTGCCGATTGTTGGTAGCGAAGAATTTGCAGAATATCATAGCCAGCGTGAAGCACTGACCAATGTTTTTCGTGATCTGGCAACCGAAATTGTTGGTGATGATCCGCGTGCAGAACTGCCGTTTCATATTACCATGAGCGTTATTGAAATGCGTCGCAATGATGGTAAAATTCCGAGTCCGCTGAGCGCAGATAGCCTGCCGGAAACCGCAATTATGCTGGCAGATGCAAGCCTGGCAGTTCTGGGTGCACCGCTGCCTGCAGATCGTGTTGAAAAAACCCTGGAACTGATTAAACAGGCAGATGCAAAATAA'),(43,'pAmtR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/AmtR.ape','gattcgttaccaattgacagTTTCTATCGATCTATAGATAATgctagc'),(44,'AmtR_operator',9,1,'','','',''),(45,'BM3R1_NOT',10,0,'','','',''),(46,'BM3R1_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/BM3R1.ape','CTATGGACTATGTTTTAACTACTAG'),(47,'BM3R1_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/BM3R1.ape','ATGGAAAGCACCCCGACCAAACAGAAAGCAATTTTTAGCGCAAGCCTGCTGCTGTTTGCAGAACGTGGTTTTGATGCAACCACCATGCCGATGATTGCAGAAAATGCAAAAGTTGGTGCAGGCACCATTTATCGCTATTTCAAAAACAAAGAAAGCCTGGTGAACGAACTGTTTCAGCAGCATGTTAATGAATTTCTGCAGTGTATTGAAAGCGGTCTGGCAAATGAACGTGATGGTTATCGTGATGGCTTTCATCACATTTTTGAAGGTATGGTGACCTTTACCAAAAATCATCCGCGTGCACTGGGTTTTATCAAAACCCATAGCCAGGGCACCTTTCTGACCGAAGAAAGCCGTCTGGCATATCAGAAACTGGTTGAATTTGTGTGCACCTTTTTTCGTGAAGGTCAGAAACAGGGTGTGATTCGTAATCTGCCGGAAAATGCACTGATTGCAATTCTGTTTGGCAGCTTTATGGAAGTGTATGAAATGATCGAGAACGATTATCTGAGCCTGACCGATGAACTGCTGACCGGTGTTGAAGAAAGCCTGTGGGCAGCACTGAGCCGTCAGAGCTAA'),(48,'pBM3R1',2,1,'+','#00ff00','/Users/peng/work/cello/ape/BM3R1.ape','TCTGATTCGTTACCAATTGACGGAATGAACGTTCATTCCGATAATGCTAGCTACTAGAG'),(49,'BM3R1_operator',9,1,'','','',''),(50,'BetI_NOT',10,0,'','','',''),(51,'BetI_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/BetI.ape','GCTACGACTTGCTCATTTGACAGAGGATAACTACTA'),(52,'BetI_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/BetI.ape','GTGCCGAAACTGGGTATGCAGAGCATTCGTCGTCGTCAGCTGATTGATGCAACCCTGGAAGCAATTAATGAAGTTGGTATGCATGATGCAACCATTGCACAGATTGCACGTCGTGCCGGTGTTAGCACCGGTATTATTAGCCATTATTTCCGCGATAAAAACGGTCTGCTGGAAGCAACCATGCGTGATATTACCAGCCAGCTGCGTGATGCAGTTCTGAATCGTCTGCATGCACTGCCGCAGGGTAGCGCAGAACAGCGTCTGCAGGCAATTGTTGGTGGTAATTTTGATGAAACCCAGGTTAGCAGCGCAGCAATGAAAGCATGGCTGGCATTTTGGGCAAGCAGCATGCATCAGCCGATGCTGTATCGTCTGCAGCAGGTTAGCAGTCGTCGTCTGCTGAGCAATCTGGTTAGCGAATTTCGTCGTGAACTGCCTCGTGAACAGGCACAAGAGGCAGGTTATGGTCTGGCAGCACTGATTGATGGTCTGTGGCTGCGTGCAGCACTGAGCGGTAAACCGCTGGATAAAACCCGTGCAAATAGCCTGACCCGTCATTTTATCACCCAGCATCTGCCGACCGATTAA'),(53,'pBetI',2,1,'+','#00ff00','/Users/peng/work/cello/ape/BetI.ape','gattcgttaccaattgacaATTgATTGGACGTTCAATATAAtgctagc'),(54,'BetI_operator',9,1,'','','',''),(55,'ButR_NOT',10,0,'','','',''),(56,'ButR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/ButR.ape','CTATGGACTATGTTTTCACACAGGAAATACTACG'),(57,'ButR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/ButR.ape','ATGAGCAAAGCAGCAAAAAGCAGCCGTAATACCAGTCCGGATGCACCGGAAAGCGCAGCAGGTAATCGTGCAGCAGCACAGCGTCTGAAAATGCGTCGTGAACTGGCAGCAGCAGCAATGGAACTGTTTGCAAGCAAAGGTTATaAAGCAACCACCGTTGATGAAATTGCAGCAGCAGCCGGTGTTGCACGTCGTACCTTTTTTCGTCATTTTCGTAGCAAAGAAGAAGCCATTTTTCCGGATCATGATGATACCCTGATTCGTGCCGAAGCAGTTCTGAATGCAGCACCGCCTCATGAACATCCGCTGGATACCGTTTGTCGTGGTATTAAAGAAGTGATGAAAATGTACGCAGCAAGTCCGGCAGTTAGCGTTGAACGTTATCGTCTGACCCGTGAAGTTCCGACCCTGCGTGAACGTGAAATTGCAAGCGTTGCACGTTATGAACGTCTGTTTACCCGTTATCTGCTGGGTCATTTTGATGAACATGCACATCATGATGGCAATGATGATCCGCTGCTGGCAGAAGTTGCAGCAAGCGCAGTTGTTACCGCACATAATCATGTTCTGCGTCGTTGGCTGCGTGCCGGTGGTCAGGGTGATGTTGAAACCCAGCTGGATCATGCATTTGCAATTGTTCGTCGCACCTTTGGCACCGGTATTGGTGCAGGTCGTGATACCCTGCCTGCAGCCGGTCCGGCAACCGTTAGCGCACAGGGTGAAGTTCTGGTTACCGTTGCACGTACCGATGCACCGCTGGATGAAGTTATGCGTACCATTGAAAAAGCACTGCGTGAACGCAGCTAA'),(58,'pButR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/ButR.ape','gctactccctcgagccgtcaattgtctgattcGTGTCACTTTGACAGCAGTGTCACtcctaggtataatgctagc'),(59,'ButR_operator',9,1,'','','',''),(60,'HapR_NOT',10,0,'','','',''),(61,'HapR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/HapR.ape','CTATGGACTATGTTTAAAGAGGACACATACTAG'),(62,'HapR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/HapR.ape','ATGGATGCAAGCATTGAAAAACGTCCGCGTACCCGTCTGAGTCCGCAGAAACGTAAACTGCAGCTGATGGAAATTGCCCTGGAAGTTTTTGCAAAACGTGGTATTGGTCGTGGTGGTCATGCAGATATTGCAGAAATTGCACAGGTTAGCGTTGCAACCGTGTTTAACTATTTTCCGACCCGTGAAGATCTGGTTGATGATGTTCTGAATTTTGTGGTTCGCCAGTATAGCAATTTTCTGACCGATCACATCGATCTGGATCTGGATGTTAAAACCAATCTGCAGACCGTTTGCAAAGAAATGGTTAAACTGGCAATGACCGATTGTCATTGGCTGAAAGTTTGGTTTGAATGGTCAGCAAGCACCCGTGATGAAGTTTGGCCTCTGTTTGTTAGCACCAATCGTACCAATCAGCTGCTGATTCGCAACATGTTTATGAAAGCAATGGAACGTGGTGAACTGTGCGAAAAACATGATGTTGATAATATGGCCAGCCTGTTTCACGGTATCTTTTATAGCATCTTTCTGCAGGTTAATCGCCTGGGTGAACAAGAAGCAGTTTATAAACTGGCAGATAGCTATCTGAACATGCTGTGCATCTATAAAAACTAA'),(63,'pHapR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/HapR.ape','ttgacagctagctcTTATTGATTTTTAATCAAATAA'),(64,'HapR_operator',9,1,'','','',''),(65,'IcaR_NOT',10,0,'','','',''),(66,'IcaR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/IcaR.ape','ctatggactatgtttTCACACAGGGGCCGG'),(67,'IcaR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/IcaR.ape','ATGAAAGACAAAATTATCGATAACGCCATCACCCTGTTTAGCGAAAAAGGTTATGACGGCACCACCCTGGATGATATTGCAAAAAGCGTGAACATCAAAAAAGCCAGCCTGTATTATCACTTTGATAGCAAAAAAAGCATCTACGAGCAGAGCGTTAAATGCTGTTTCGATTATCTGAACAACATCATCATGATGAACCAGAACAAAAGCAACTATAGCATCGATGCCCTGTATCAGTTTCTGTTTGAGTTCATCTTCGATATCGAGGAACGCTATATTCGTATGTATGTTCAGCTGAGCAACACACCGGAAGAATTTTCAGGTAACATTTATGGCCAGATCCAGGATCTGAATCAGAGCCTGAGCAAAGAAATCGCCAAATTCTATGACGAAAGCAAAATCAAAATGACCAAAGAGGACTTCCAGAATCTGATTCTGCTGTTTCTGGAAAGCTGGTATCTGAAAGCCAGCTTTAGCCAGAAATTTGGTGCAGTTGAAGAAAGCAAAAGCCAGTTTAAAGATGAGGTTTATAGCCTGCTGAACATCTTTCTGAAGAAATAA'),(68,'pIcaR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/IcaR.ape','gattcgttaccaattgacaaTTCACCTACCTTTCGTTAGgTTAGGTTGT'),(69,'IcaR_operator',9,1,'','','',''),(70,'Orf2_NOT',10,0,'','','',''),(71,'Orf2_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/Orf2.ape','ctatggactatgtttTGAAAGAGGAGAAACACTAG'),(72,'Orf2_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/Orf2.ape','ATGCAGCAGCAGCATGAAGTTGCAGCACGTGTTCGTCGTGTTATTGATGCAGCCGGTGTTAGCGCACGTGAATTTGCACGTCGTATTGTTATTGATCGTCGTAGCAGTCCGGGTCCGAGCACCGCACCGGGTGCAAGTCCGCCTCCGAATTGGCCTGGTAGCCGTACACCGGCAGCATGGACCTGGGATGGTTGTAGCGGTCTGCGTCCGGTTCGTCGTCGTCGTAGCACCGTTCGTAGCCCGAGCGCACCTCGTCTGCCTCCGGCAAGCACCGAAGGTGGTCGTCCGCTGCAGATTGTTCGTGAAACCGTTCGTCTGATTGCAGAACGTGGTTTTCATGCAGTTCGTGTTGCAGATATTGCAGCAGCATGTCATACCAGCACCGCAGCAATTCATTATCATTTTCCGGGTCGTGATGAACTGCTGGAAGCAGCAGTTCGTTGGTGTATGGATGAAGATACCCGTCGTCGTGCAGATGCAACCGCAGGCGCACGTCATGCCGGTGATGAGCTGCGTCTGCTGATTGAACTGCAGACACCGCGTACCGAACAGCAGCGTCGTCAGTGGTGTGTTTGGCTGGATCTGTGGGCAGAAGCAGCACGTAGCACCACCGTTGGTCAGCTGCATGTTGAATATTATCGTCAGTGGCGTGGCACCGTTGCAGATGTTATTCGTCGTGGTGTTGGTCAGGGTGTTTTTCGTCCGGTTGATGCAGATGGTGCAGCACTGACCCTGACCGCACTGATTGATGGTCTGGCAAGCCAGGTTCTGGCAACCGCTCCGGGTCATCCGGGTACAGGTGCACAGACCATGCATGATGCACTGATTGCACATGTTAGCGCATGTCTGGCAGCACCGGCAGCAGATTAA'),(73,'pOrf2',2,1,'+','#00ff00','/Users/peng/work/cello/ape/Orf2.ape','cgagccgtcaattgtctgattcgttaccaattgacaCTAACTGCTGTTCAGTTAGGTTggctagc'),(74,'Orf2_operator',9,1,'','','',''),(75,'PsrA_NOT',10,0,'','','',''),(76,'PsrA_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/PsrA.ape','ctatggactatgtttGAAAGAGGATACGAACTACTAG'),(77,'PsrA_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/PsrA.ape','ATGGCACAGAGCGAAACCGTTGAACGTATTCTGGATGCAGCAGAACAGCTGTTTGCAGAACGTGGTTTTGCAGAAACCAGCCTGCGTCTGATTACCAGCAAAGCCGGTGTTAATCTGGCAGCAGTGAATTATCATTTTGGCAGCAAAAAAGCACTGATTCAGGCAGTTTTTAGCCGTTTTCTGGGTCCGTTTTGTGCAAGCCTGGAACGTGAACTGGAACGTCGTCAGGCACGTCCGGAACAGAAACCGAGCCTGGAAGAACTGCTGGAAATGCTGGTTGAACAGGCACTGGCAGTTCAGCCTCGTAGCAATAATGATCTGAGCATTTTTATGCGTCTGCTGGGTCTGGCATTTAGCCAGAGCCAGGGTCATCTGCGTCGTTATCTGGAAGATATGTATGGTAAAGTGTTCCGTCGTTATATGCTGCTGGTTAATGAAGCAGCACCGCGTGTTCCGCCTCTGGAACTGTTTTGGCGTGTTCATTTTATGCTGGGTGCAGCAGCATTTAGCATGAGCGGTATTAAAGCACTGCGTGCAATTGCAGAAACCGATTTTGGTATTAACACCAGCATTGAACAGGTTATGCGTCTGATGGTTCCGTTTCTGGCAGCAGGTATGCGTGCAGATAGCGGTGTTACCGATGAAGCAATGGCAGCAGCACAGCTGCGTCCGCGTAGCAAAACCAGCACCAGCGCAACCACCGCAAAAGCATAA'),(78,'pPsrA',2,1,'+','#00ff00','/Users/peng/work/cello/ape/PsrA.ape','cgacgtacggtgGAAAGGAACAAACGTTTGAttgacagctagctcagtcctaggtataatgctagc'),(79,'PsrA_operator',9,1,'','','',''),(80,'QacR_NOT',10,0,'','','',''),(81,'QacR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/QacR.ape','gccatgccattggctttTCACACAGGACACCGGTTAGTACTAG'),(82,'QacR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/QacR.ape','atgaacctgaaagataaaattctgggcgttgccaaagaactgtttatcaaaaatggctataacgcaaccaccaccggtgaaattgttaaactgagcgaaagcagcaaaggcaatctgtattatcactttaaaaccaaagagaacctgtttctggaaatcctgaacatcgaagaaagcaaatggcaagagcagtggaaaaaagaacaaatcaaatgcaaaaccaaccgcgagaaattctatctgtataatgaactgagcctgaccaccgaatattactatccgctgcagaatgccatcatcgagttttataccgagtactataaaaccaacagcatcaacgagaaaatgaacaaactggaaaacaaatacatcgatgcctaccacgtgatctttaaagaaggtaatctgaacggcgaatggtgcattaatgatgttaatgccgtgagcaaaattgcagcaaatgccgttaatggcattgttacctttacccatgagcagaatatcaacgaacgcattaaactgatgaacaaattcagccagatctttctgaatggcctgagcaaataa'),(83,'pQacR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/QacR.ape','gtcaattgtctgattcgttaccaattgacagctagctcagtcctaCTTTAGTATAGAGACTGAGCGGTCGGTCTATA'),(84,'QacR_operator',9,1,'','','',''),(85,'ScbR_NOT',10,0,'','','',''),(86,'ScbR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/ScbR.ape','ctatggactatgtttAAAGAGGAAAAGTACTAG'),(87,'ScbR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/ScbR.ape','ATGGCAAAACAGGATCGTGCAATTCGTACCCGTCAGACCATTCTGGATGCAGCAGCACAGGTTTTTGAAAAACAGGGTTATCAGGCAGCAACCATTACCGAAATTCTGAAAGTTGCCGGTGTTACCAAAGGTGCACTGTATTTTCACTTTCAGAGCAAAGAAGAACTGGCACTGGGCGTTTTTGATGCACAAGAACCGCCTCAGGCAGTTCCGGAACAGCCGCTGCGTCTGCAAGAACTGATTGATATGGGTATGCTGTTTTGTCATCGTCTGCGTACCAATGTTGTTGCACGTGCCGGTGTTCGTCTGAGCATGGATCAGCAGGCACATGGTCTGGATCGTCGTGGTCCGTTTCGTCGTTGGCATGAAACCCTGCTGAAACTGCTGAATCAGGCAAAAGAAAATGGTGAACTGCTGCCGCATGTTGTTACCACCGATAGCGCAGATCTGTATGTGGGCACCTTTGCAGGTATTCAGGTTGTTAGCCAGACCGTTAGCGATTATCAGGATCTGGAACATCGTTATGCACTGCTGCAGAAACATATTCTGCCTGCAATTGCAGTTCCGAGCGTTCTGGCAGCACTGGATCTGAGCGAAGAACGTGGTGCACGTCTGGCAGCAGAACTGGCACCGACCGGTAAAGATTAA'),(88,'pScbR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/ScbR.ape','gattcgttaccaattgacagctagctATCATACCGCTATAATGGTATGTT'),(89,'ScbR_operator',9,1,'','','',''),(90,'SmcR_NOT',10,0,'','','',''),(91,'SmcR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/SmcR.ape','CTATGGACTATGTTTgaaagaggagaaatactag'),(92,'SmcR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/SmcR.ape','ATGGATAGCATTGCAAAACGTCCGCGTACCCGTCTGAGTCCGCTGAAACGTAAACAGCAGCTGATGGAAATTGCCCTGGAAGTTTTTGCACGTCGTGGTATTGGTCGTGGTGGTCATGCAGATATTGCAGAAATTGCACAGGTTAGCGTTGCAACCGTGTTTAACTATTTTCCGACCCGTGAAGATCTGGTTGATGAAGTTCTGAATCATGTTGTTCGCCAGTTTAGCAATTTCCTGAGCGATAATATCGATCTGGATCTGCATGCCAAAGAAAACATTGCCAATATTACCAACGCCATGATTGAACTGGTGGTTCAGGATAATCATTGGCTGAAAGTTTGGTTTGAATGGTCAGCAAGCACCCGTGATGAAGTTTGGCCTCTGTTTGTTACCACCAATCGTACCAATCAGCTGCTGGTTCAGAACATGTTTATCAAAGCAATTGAACGTGGCGAAGTTTGCGATCAGCATAATCCGGAAGATCTGGCAAACCTGTTTCATGGTATTTGCTATAGCCTGTTTGTTCAGGCAAATCGCACCAATAACACCGCAGAACTGAGCAAACTGGTTAGCAGCTATCTGGATATGCTGTGTATCTATAAACGCGAACACGAATAA'),(93,'pSmcR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/SmcR.ape','cgagccgtcaattgtctgattcgttaccaattgacaTTATTGATAAATCTGCGTAAAATgctagc'),(94,'SmcR_operator',9,1,'','','',''),(95,'SrpR_NOT',10,0,'','','',''),(96,'SrpR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/SrpR.ape','ctatggactatgtttTCACACAGGAAATACCAGG'),(97,'SrpR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/SrpR.ape','ATGGCACGTAAAACCGCAGCAGAAGCAGAAGAAACCCGTCAGCGTATTATTGATGCAGCACTGGAAGTTTTTGTTGCACAGGGTGTTAGTGATGCAACCCTGGATCAGATTGCACGTAAAGCCGGTGTTACCCGTGGTGCAGTTTATTGGCATTTTAATGGTAAACTGGAAGTTCTGCAGGCAGTTCTGGCAAGCCGTCAGCATCCGCTGGAACTGGATTTTACACCGGATCTGGGTATTGAACGTAGCTGGGAAGCAGTTGTTGTTGCAATGCTGGATGCAGTTCATAGTCCGCAGAGCAAACAGTTTAGCGAAATTCTGATTTATCAGGGTCTGGATGAAAGCGGTCTGATTCATAATCGTATGGTTCAGGCAAGCGATCGTTTTCTGCAGTATATTCATCAGGTTCTGCGTCATGCAGTTACCCAGGGTGAACTGCCGATTAATCTGGATCTGCAGACCAGCATTGGTGTTTTTAAAGGTCTGATTACCGGTCTGCTGTATGAAGGTCTGCGTAGCAAAGATCAGCAGGCACAGATTATCAAAGTTGCACTGGGTAGCTTTTGGGCACTGCTGCGTGAACCGCCTCGTTTTCTGCTGTGTGAAGAAGCACAGATTAAACAGGTGAAATCCTTCGAATAA'),(98,'pSrpR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/SrpR.ape','gattcgttaccaattgacagctagctcagtcctaggtATATACATACATGCTTGTTTGTTTGTAAAC'),(99,'SrpR_operator',9,1,'','','',''),(100,'TarA_NOT',10,0,'','','',''),(101,'TarA_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/TarA.ape','CTATGGACTATGTTTTtcaaagaggagaaatactag'),(102,'TarA_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/TarA.ape','ATGGCACAGCAGGATCGTGCAGTTCGTACCCGTCGTGCAGTTCTGCGTGCAGCAGCAGCAGTTTTTGCAGAACGTGGTTATGCAGCAGCAACCATTAGCGAAATTCTGAAACGTGCCGGTGTTACCAAAGGTGCACTGTATTTTCACTTTGATAGCAAAGCAGCACTGGCACAGGGTGTTCTGCAAGAACAGCTGACACCGGAATATCATCTGCCTCGTGAACTGAAACTGCAAGAATGGGTTGATGCAGGTATGACCCTGGCACGTCGTCTGCCACGTGAACCGTTTCTGCTGGCAGGCGTTCGTATTAGCGCAGATCGTCCGGGTCGTGAAGTTCTGGGTAGCGCATGGCCTGCATGGTCACGTCTGACCAGCCATGTTCTGACCGAAGCAAAAAAACGTGGTGAAGTTCTGCCGCATGTTGTTCCGGAAGAAACCGCACAGGTTTTTCTGGGTGCATGGGTTGGTGCACAGTTTGTTAGCCAGACCCTGGCAGGTTGGGAAGATCTGGATGATCGTACCGCAGCACTGTATAGCCATCTGCTGGGTGCAATTGCAGCACCGCCTGTTCTGACCCGTCTGGATACCGCACCGGATCGTGGTGCACGTGTTATTGCAGAAGCACGTCGTCGTAGCGGTGATCTGAGCGGTATTGCATGTTAA'),(103,'pTarA',2,1,'+','#00ff00','/Users/peng/work/cello/ape/TarA.ape','GCCTCTAATACATCCgattcgttaccaattgacagcTAAACATACCGTGTGGTATGTTctagc'),(104,'TarA_operator',9,1,'','','',''),(105,'TetR_NOT',10,0,'','','',''),(106,'TetR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/TetR.ape','CTATGGACTATGTTTTCACACAGGAAAGGCCTCG'),(107,'TetR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/TetR.ape','ATGtccagattagataaaagtaaagtgattaacagcgcattagagctgcttaatgaggtcggaatcgaaggtttaacaacccgtaaactcgcccagaagctaggtgtagagcagcctacattgtattggcatgtaaaaaataagcgggctttgctcgacgccttagccattgagatgttagataggcaccatactcacttttgccctttagaaggggaaagctggcaagattttttacgtaataacgctaaaagttttagatgtgctttactaagtcatcgcgatggagcaaaagtacatttaggtacacggcctacagaaaaacagtatgaaactctcgaaaatcaattagcctttttatgccaacaaggtttttcactagagaatgcattatatgcactcagcgctgtggggcattttactttaggttgcgtattggaagatcaagagcatcaagtcgctaaagaagaaagggaaacacctactactgatagtatgccgccattattacgacaagctatcgaattatttgatcaccaaggtgcagagccagccttcttattcggccttgaattgatcatatgcggattagaaaaacaacttaaatgtgaaagtgggtcctaa'),(108,'pTetR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/TetR.ape','tccctatcagtgatagagattgacaTCCCTATCAGTGATAGAtataatgagcac'),(109,'TetR_operator',9,1,'','','',''),(110,'inducer_pTac',10,0,'','','',''),(111,'inducer_pLuxStar',10,0,'','','',''),(112,'inducer_pBAD',10,0,'','','',''),(113,'inducer_pTet',10,0,'','','',''),(114,'reporter_YFP',10,0,'','','',''),(115,'A-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','GGAG'),(116,'B-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','TACG'),(117,'C-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','AATG'),(118,'D-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','AGGT'),(119,'E-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','GCTT'),(120,'F-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','CGCT'),(121,'T-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','AATC'),(122,'U-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','GGGC'),(123,'V-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','TCTG'),(124,'W-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','TGAG'),(125,'X-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','TGTC'),(126,'Y-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','ATTG'),(127,'Z-scar',14,1,'+','#999999','/Users/peng/work/people/Alec/circuit_scars.xlsx','TTCC'),(128,'Linker2',15,1,'+','#c0c0c0','/Users/peng/work/people/Alec/circuit_backbone.ape','cgagacc'),(129,'bw_Linker2_p15A',15,1,'+','','/Users/peng/work/people/Alec/circuit_backbone.ape','gcttcctcgctcactgactcgctgcacgaggca'),(130,'bw_p15A_Kan',15,1,'+','','/Users/peng/work/people/Alec/circuit_backbone.ape','ggggtctgacgctcagtggaacgaaaaatcaatctaaagtatatatgagtaaacttggtctgacagttacc'),(131,'Kanamycin',20,1,'-','pink','/Users/peng/work/people/Alec/circuit_backbone.ape','ttagaaaaactcatcgagcatcaaatgaaactgcaatttattcatatcaggattatcaataccatatttttgaaaaagccgtttctgtaatgaaggagaaaactcaccgaggcagttccataggatggcaagatcctggtatcggtctgcgattccgactcgtccaacatcaatacaacctattaatttcccctcgtcaaaaataaggttatcaagtgagaaatcaccatgagtgacgactgaatccggtgagaatggcaaaagcttatgcatttctttccagacttgttcaacaggccagccattacgctcgtcatcaaaatcactcgcatcaaccaaaccgttattcattcgtgattgcgcctgagcgagacgaaatacgcgatcgctgttaaaaggacaattacaaacaggaatcgaatgcaaccggcgcaggaacactgccagcgcatcaacaatattttcacctgaatcaggatattcttctaatacctggaatgctgttttcccggggatcgcagtggtgagtaaccatgcatcatcaggagtacggataaaatgcttgatggtcggaagaggcataaattccgtcagccagtttagtctgaccatctcatctgtaacatcattggcaacgctacctttgccatgtttcagaaacaactctggcgcatcgggcttcccatacaatcgatagattgtcgcacctgattgcccgacattatcgcgagcccatttatacccatataaatcagcatccatgttggaatttaatcgcggcctcgagcaagacgtttcccgttgaatatggctcat'),(132,'bw_Kan_Linker1',15,1,'+','','/Users/peng/work/people/Alec/circuit_backbone.ape','aacaccccttgtattactgtttatgtaagcagacagttttattgttcatgatgatatatttttatcttgtgcaatgtacatcagagattttgagacacaa'),(133,'Linker1',15,1,'+','#c0c0c0','/Users/peng/work/people/Alec/circuit_backbone.ape','ggtctcc'),(134,'Backbone',10,0,'+','','',''),(135,'RBS2',4,1,'-','#ff8040','/Users/peng/work/cello/ape/PhlF.ape','CGAGGCCTTTCCTGTGTGA'),(136,'post_CDS_spacer',8,1,'+','#c8c8c8','/Users/peng/work/cello/ape/PhlF.ape','CTAGGGCCCATACCC'),(137,'pre_prom_spacer',8,1,'+','#c8c8c8','/Users/peng/work/cello/ape/PhlF.ape','CGACGTACGGTGGAA'),(138,'L3S2P21',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CTCGGTACCAAATTCCAGAAAAGAGGCCTCCCGAAAGGGGGGCCTTTTTTCGTTTTGGTCC'),(139,'ECK120029600',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','TTCAGCCAAAAAACTTAAGACCGCCGGTCTTGTCCACTACCTTGCAGTAATGCGGTGGACAGGATCGGCGGTTTTCTTTTCTCTTCTCAA'),(140,'ECK120033737',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','GGAAACACAGAAAAAAGCCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGG'),(141,'L3S2P11',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CTCGGTACCAAATTCCAGAAAAGAGACGCTTTCGAGCGTCTTTTTTCGTTTTGGTCC'),(142,'L3S2P55',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CTCGGTACCAAAGACGAACAATAAGACGCTGAAAAGCGTCTTTTTTCGTTTTGGTCC'),(143,'L3S3P21',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CCAATTATTGAAGGCCTCCCTAACGGGGGGCCTTTTTTTGTTTCTGGTCTCCC'),(144,'L3S3P22',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CCAATTATTGAAGGCCGCTAACGCGGCCTTTTTTTGTTTCTGGTCTCCC'),(145,'L3S3P00',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CCAATTATTGAAGGGGAGCGGGAAACCGCTCCCCTTTTTTTGTTTCTGGTCTCCC'),(146,'L3S1P13',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','GACGAACAATAAGGCCTCCCTAACGGGGGGCCTTTTTTATTGATAACAAAA'),(147,'L3S3P11',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CCAATTATTGAACACCCTTCGGGGTGTTTTTTTGTTTCTGGTCTCCC'),(148,'L3S3P23',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CCAATTATTGAAGACGCTTAACAGCGTCTTTTTTTGTTTCTGGTCTCCC'),(149,'ECK120033736',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','AACGCATGAGAAAGCCCCCGGAAGATCACCTTCCGGGGGCTTTTTTATTGCGC'),(150,'L3S2P24',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CTCGGTACCAAATTCCAGAAAAGACACCCGAAAGGGTGTTTTTTCGTTTTGGTCC'),(151,'ECK120010818',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','GTCAGTTTCACCTGTTTTACGTAAAAACCCGCTTCGGCGGGTTTTTACTTTTGG'),(152,'L3S1P22',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','GACGAACAATAAGGCCGCAAATCGCGGCCTTTTTTATTGATAACAAAA'),(153,'L3S1P47',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','TTTTCGAAAAAAGGCCTCCCAAATCGGGGGGCCTTTTTTTATAGCAACAAAA'),(154,'ECK120015440',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','TCCGGCAATTAAAAAAGCGGCTAACCACGCCGCTTTTTTTACGTCTGCA'),(155,'L3S3P45',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','TTCCAGAAAAGACACCCTAACGGGTGTTTTTTCGTTTTTGGTCTCCC'),(156,'L3S2P44',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CTCGGTACCAAACCAATTATTGAAGACGCTGAAAAGCGTCTTTTTTTGTTTCGGTCC'),(157,'L3S3P31',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CCAATTATTGAACACCCTAACGGGTGTTTTTTTTTTTTTGGTCTCCC'),(158,'L3S3P43',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','TCTAACTAAAAACACCCTAACGGGTGTTTTTTCTTTTCTGGTCTCCC'),(159,'ECK120010799',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','GTTATGAGTCAGGAAAAAAGGCGACAGAGTAATCTGTCGCCTTTTTTCTTTGCTTGCTTT'),(160,'ECK120010876',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','TAAGGTTGAAAAATAAAAACGGCGCTAAAAAGCGCCGTTTTTTTTGACGGTGGTA'),(161,'L3S3P42',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','GAAAAATAAAAACACCCTAACGGGTGTTTTTATTTTTCTGGTCTCCC'),(162,'L3S2P42',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CTCGGTACCAAAGAAAAATAAAAAGACGCTGAAAAGCGTCTTTTTATTTTTCGGTCC'),(163,'L3S1P00',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','GACGAACAATAAGGGGAGCGGGAAACCGCTCCCCTTTTTTATTGATAACAAAA'),(164,'ECK120015170',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','ACAATTTTCGAAAAAACCCGCTTCGGCGGGTTTTTTTATAGCTAAAA'),(165,'L3S3P47',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','TTTTCGAAAAAACACCCTAACGGGTGTTTTTTTATAGCTGGTCTCCC'),(166,'BBa_B0010',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CCAGGCATCAAATAAAACGAAAGGCTCAGTCGAAAGACTGGGCCTTTCGTTTTATCTGTTGTTTGTCGGTGAACGCTCTC'),(167,'ECK120017009',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','GATCTAACTAAAAAGGCCGCTCTGCGGCCTTTTTTCTTTTCACT'),(168,'L3S3P25',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CCAATTATTGAAGCGGCTAACGCCGCTTTTTTTGTTTCTGGTCTCCC'),(169,'ECK120051401',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CGCAGATAGCAAAAAAGCGCCTTTAGGGCGCTTTTTTACATTGGTGG'),(170,'ECK120010855',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','GTAACAACGGAAACCGGCCATTGCGCCGGTTTTTTTTGGCCT'),(171,'ECK120010850',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','AGTTAACCAAAAAGGGGGGATTTTATCTCCCCTTTAATTTTTCCT'),(172,'ECK120035137',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','AGGCGACTGACGAAACCTCGCTCCGGCGGGGTTTTTTGTTATCTGCA'),(173,'ECK120035133',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','ACTGATTTTTAAGGCGACTGATGAGTCGCCTTTTTTTTGTCT'),(174,'tonB_P14',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','CCTGTTGAGTAATAGTCAAAAGCCTCCGGTCGGAGGCTTTTGACTTTCTGCTTAC'),(175,'ECK120023928',6,1,'+','#ff0000','TableS2, 02_find_U_all.xls','GCTGATGCCAGAAAGGGTCCTGAATTTCAGGGCCCTTTTTTTACATGGATTG'),(176,'LtsvJ-PhlF_NOT',10,0,'','','',''),(177,'SarJ-BM3R1_NOT',10,0,'','','',''),(178,'PlmJ-TarA_NOT',10,0,'','','',''),(179,'SccJ-SrpR_NOT',10,0,'','','',''),(180,'VtmoJ-HlyIIR_NOT',10,0,'','','',''),(181,'LtsvJ',3,1,'+','magenta','/Users/peng/work/cello/ape/LtsvJ-PhlF.ape','AGTACGTCTGAGCGTGATACCCGCTCACTGAAGATGGCCCGGTAGGGCCGAAACGTACCTCTACAAATAATTTTGTTTAA'),(182,'SarJ',3,1,'+','magenta','/Users/peng/work/cello/ape/SarJ-BM3R1.ape','AGACTGTCGCCGGATGTGTATCCGACCTGACGATGGCCCAAAAGGGCCGAAACAGTCCTCTACAAATAATTTTGTTTAA'),(183,'PlmJ',3,1,'+','magenta','/Users/peng/work/cello/ape/PlmJ-TarA.ape','AGTCATAAGTCTGGGCTAAGCCCACTGATGAGTCGCTGAAATGCGACGAAACTTATGACCTCTACAAATAATTTTGTTTAA'),(184,'SccJ',3,1,'+','magenta','/Users/peng/work/cello/ape/SccJ-SrpR.ape','AGATGCTGTAGTGGGATGTGTGTCTCACCTGAAGAGTACAAAAGTCCGAAACGGTATCCTCTACAAATAATTTTGTTTAA'),(185,'VtmoJ',3,1,'+','magenta','/Users/peng/work/cello/ape/VtmoJ-HlyIIR.ape','AGTCCGTAGTGGATGTGTATCCACTCTGATGAGTCCGAAAGGACGAAACGGACCTCTACAAATAATTTTGTTTAA'),(186,'HlyIIR_RBS',4,1,'+','#ff8040','/Users/peng/work/cello/ape/HlyIIR.ape','ctatggactatgtttGAAAGAGGGACAAACACTAA'),(187,'HlyIIR_CDS',5,1,'+','#0000ff','/Users/peng/work/cello/ape/HlyIIR.ape','ATGAAATACATCCTGTTTGAGGTGTGCGAAATGGGTAAAAGCCGTGAACAGACCATGGAAAATATTCTGAAAGCAGCCAAAAAGAAATTCGGCGAACGTGGTTATGAAGGCACCAGCATTCAAGAAATTGCCAAAGAAGCCAAAGTTAACGTTGCAATGGCCAGCTATTACTTTAATGGCAAAGAGAACCTGTACTACGAGGTGTTCAAAAAATACGGTCTGGCAAATGAACTGCCGAACTTTCTGGAAAAAAACCAGTTTAATCCGATTAATGCCCTGCGTGAATATCTGACCGTTTTTACCACCCACATTAAAGAAAATCCGGAAATTGGCACCCTGGCCTATGAAGAAATTATCAAAGAAAGCGCACGCCTGGAAAAAATCAAACCGTATTTTATCGGCAGCTTCGAACAGCTGAAAGAAATTCTGCAAGAGGGTGAAAAACAGGGTGTGTTTCACTTTTTTAGCATCAACCATACCATCCATTGGATTACCAGCATTGTTCTGTTTCCGAAATTCAAAAAATTCATCGATAGCCTGGGTCCGAATGAAACCAATGATACCAATCATGAATGGATGCCGGAAGATCTGGTTAGCCGTATTATTAGCGCACTGACCGATAAACCGAACATTTAA'),(188,'pHlyIIR',2,1,'+','#00ff00','/Users/peng/work/cello/ape/HlyIIR.ape','gattcgttaccaattgacATATTTAAAATTCTTGTTTAAAatgctagc'),(189,'HlyIIR_operator',9,1,'','','',''),(190,'inducer_pLux',10,0,'','','',''),(191,'RiboJ',3,1,'+','magenta','/User/peng/work/cello/ape/RiboJ-YFP.ape','AGCTGTCACCGGATGTGCTTTCCGGTCTGATGAGTCCGTGAGGACGAAACAGCCTCTACAAATAATTTTGTTTAA'),(192,'PlmJ-AmtR_NOT',10,0,'','',NULL,''),(193,'PhlF_rbs1_NOT',10,0,'+','',NULL,''),(194,'SrpR_rbs1_NOT',10,0,'+','',NULL,''),(195,'BM3R1_rbs1_NOT',10,0,'+','',NULL,''),(196,'QacR_rbs2_NOT',10,0,'+','',NULL,''),(197,'AmtR_rbs1_NOT',10,0,'+','',NULL,''),(198,'HlyIIR_rbs2_NOT',10,0,'+','',NULL,''),(199,'IcaRA_rbs1_NOT',10,0,'+','',NULL,''),(200,'BetI_rbs2_NOT',10,0,'+','',NULL,''),(201,'LitR_rbs2_NOT',10,0,'+','',NULL,''),(202,'PhlF_rbs0_NOT',10,0,'+','',NULL,NULL),(203,'SrpR_rbs0_NOT',10,0,'+','',NULL,NULL),(204,'BM3R1_rbs0_NOT',10,0,'+','',NULL,NULL),(205,'QacR_rbs0_NOT',10,0,'+','',NULL,NULL),(206,'pIcaRA',2,1,'+',NULL,NULL,NULL),(207,'pLitR',2,1,'+',NULL,NULL,NULL),(208,'SrpR_rbs2_NOT',10,0,'+',NULL,NULL,NULL),(209,'SrpR_rbs3_NOT',10,0,'+',NULL,NULL,NULL),(210,'RiboJ00',22,1,'+',NULL,NULL,'A'),(211,'RiboJ10',22,1,'+',NULL,NULL,'A'),(212,'RiboJ53',22,1,'+',NULL,NULL,'A'),(213,'SarJ',22,1,'+',NULL,NULL,'A'),(214,'BydvJ',22,1,'+',NULL,NULL,'A'),(215,'RiboJ51',22,1,'+',NULL,NULL,'A'),(216,'PlmJ',22,1,'+',NULL,NULL,'A'),(217,'RiboJ60',22,1,'+',NULL,NULL,'A'),(218,'ElvJ',22,1,'+',NULL,NULL,'A'),(219,'RiboJ57',22,1,'+',NULL,NULL,'A'),(220,'RiboJ54',22,1,'+',NULL,NULL,'A'),(221,'ScmJ',22,1,'+',NULL,NULL,'A'),(222,'CchJ',22,1,'+',NULL,NULL,'A'),(223,'AraJ',22,1,'+',NULL,NULL,'A'),(224,'RiboJ64',22,1,'+',NULL,NULL,'A'),(225,'LstvJ',22,1,'+',NULL,NULL,'A');
/*!40000 ALTER TABLE `Part` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PartType`
--

DROP TABLE IF EXISTS `PartType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PartType` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Family',
  `Type` varchar(50) NOT NULL COMMENT 'promoter, insulator, rbs, cds, terminator, spacer, reporter',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PartType`
--

LOCK TABLES `PartType` WRITE;
/*!40000 ALTER TABLE `PartType` DISABLE KEYS */;
INSERT INTO `PartType` VALUES (1,'Origin'),(2,'Promoter'),(3,'Insulator'),(4,'RBS'),(5,'CDS'),(6,'Terminator'),(7,'ReporterCDS'),(8,'Spacer'),(9,'Operator'),(10,'Composite'),(11,'Plasmid'),(12,'Inducer'),(13,'Random'),(14,'Scar'),(15,'Backbone'),(16,'Unknown'),(17,'ConPromoterRBS'),(18,'InducerCDS'),(19,'InducerPromoter'),(20,'ResistanceCDS'),(21,'ReporterRBS'),(22,'Ribozyme');
/*!40000 ALTER TABLE `PartType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PartXref`
--

DROP TABLE IF EXISTS `PartXref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PartXref` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'CompositeXref',
  `Child` int(10) unsigned NOT NULL COMMENT 'a list of part ID that shows 5\\\\\\''-3\\\\\\'' location on DNA, e.g. Promoter1-Insulator-Promoter2-RBS-CDS-Terminator',
  `Parent` int(10) unsigned NOT NULL,
  `Position` int(10) unsigned NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Child_idx` (`Child`),
  KEY `FK_Parent_idx` (`Parent`),
  CONSTRAINT `FK_ComposeXref_Child` FOREIGN KEY (`Child`) REFERENCES `Part` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_ComposeXref_Parent` FOREIGN KEY (`Parent`) REFERENCES `Part` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=218 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PartXref`
--

LOCK TABLES `PartXref` WRITE;
/*!40000 ALTER TABLE `PartXref` DISABLE KEYS */;
INSERT INTO `PartXref` VALUES (1,110,3,0),(2,110,4,1),(3,110,136,2),(4,110,18,3),(5,110,137,4),(6,110,8,5),(7,111,3,0),(8,111,5,1),(9,111,136,2),(10,111,18,3),(11,111,137,4),(12,111,10,5),(13,112,3,0),(14,112,6,1),(15,112,136,2),(16,112,18,3),(17,112,137,4),(18,112,11,5),(19,113,3,0),(20,113,7,1),(21,113,136,2),(22,113,18,3),(23,113,137,4),(24,113,13,5),(25,114,8,0),(26,114,14,1),(27,114,17,2),(28,114,136,3),(29,114,18,4),(30,134,128,0),(31,134,129,1),(32,134,2,2),(33,134,130,3),(34,134,131,4),(35,134,132,5),(36,134,133,6),(37,20,8,0),(38,20,21,1),(39,20,22,2),(40,20,136,3),(41,20,18,4),(42,20,137,5),(43,20,23,6),(44,25,8,0),(45,25,26,1),(46,25,27,2),(47,25,136,3),(48,25,18,4),(49,25,137,5),(50,25,28,6),(51,30,8,0),(52,30,31,1),(53,30,32,2),(54,30,136,3),(55,30,18,4),(56,30,137,5),(57,30,33,6),(58,35,8,0),(59,35,36,1),(60,35,37,2),(61,35,136,3),(62,35,18,4),(63,35,137,5),(64,35,38,6),(65,40,8,0),(66,40,41,1),(67,40,42,2),(68,40,136,3),(69,40,18,4),(70,40,137,5),(71,40,43,6),(72,45,8,0),(73,45,46,1),(74,45,47,2),(75,45,136,3),(76,45,18,4),(77,45,137,5),(78,45,48,6),(79,50,8,0),(80,50,51,1),(81,50,52,2),(82,50,136,3),(83,50,18,4),(84,50,137,5),(85,50,53,6),(86,55,8,0),(87,55,56,1),(88,55,57,2),(89,55,136,3),(90,55,18,4),(91,55,137,5),(92,55,58,6),(93,60,8,0),(94,60,61,1),(95,60,62,2),(96,60,136,3),(97,60,18,4),(98,60,137,5),(99,60,63,6),(100,65,8,0),(101,65,66,1),(102,65,67,2),(103,65,136,3),(104,65,18,4),(105,65,137,5),(106,65,68,6),(107,70,8,0),(108,70,71,1),(109,70,72,2),(110,70,136,3),(111,70,18,4),(113,70,137,5),(114,70,73,6),(115,75,8,0),(116,75,76,1),(117,75,77,2),(118,75,136,3),(119,75,18,4),(120,75,137,5),(121,75,78,6),(122,80,8,0),(123,80,81,1),(124,80,82,2),(125,80,136,3),(126,80,18,4),(127,80,137,5),(128,80,83,6),(129,85,8,0),(130,85,86,1),(131,85,87,2),(132,85,136,3),(133,85,18,4),(134,85,137,5),(135,85,88,6),(136,90,8,0),(137,90,91,1),(138,90,92,2),(139,90,136,3),(140,90,18,4),(141,90,137,5),(142,90,93,6),(143,95,8,0),(144,95,96,1),(145,95,97,2),(146,95,136,3),(147,95,18,4),(148,95,137,5),(149,95,98,6),(150,100,8,0),(151,100,101,1),(152,100,102,2),(153,100,136,3),(154,100,18,4),(155,100,137,5),(156,100,103,6),(157,105,8,0),(158,105,106,1),(159,105,107,2),(160,105,136,3),(161,105,18,4),(162,105,137,5),(163,105,108,6),(164,176,8,0),(165,176,181,1),(166,176,21,2),(167,176,22,3),(168,176,136,4),(169,176,18,5),(170,176,137,6),(171,176,23,7),(172,177,8,0),(173,177,182,1),(174,177,46,2),(175,177,47,3),(176,177,136,4),(177,177,18,5),(178,177,137,6),(179,177,48,7),(180,178,8,0),(181,178,183,1),(182,178,101,2),(183,178,102,3),(184,178,136,4),(185,178,18,5),(186,178,137,6),(187,178,103,7),(188,179,8,0),(189,179,184,1),(190,179,96,2),(191,179,97,3),(192,179,136,4),(193,179,18,5),(194,179,137,6),(195,179,98,7),(196,180,8,0),(197,180,185,1),(198,180,186,2),(199,180,187,3),(200,180,136,4),(201,180,18,5),(202,180,137,6),(203,180,188,7),(204,190,3,0),(205,190,5,1),(206,190,136,2),(207,190,18,3),(208,190,137,4),(209,190,9,5),(210,192,8,0),(211,192,183,1),(212,192,41,2),(213,192,42,3),(214,192,136,4),(215,192,18,5),(216,192,137,6),(217,192,43,7);
/*!40000 ALTER TABLE `PartXref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Var`
--

DROP TABLE IF EXISTS `Var`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Var` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(20) NOT NULL,
  `VarTypeID` int(10) unsigned NOT NULL,
  `Min` float DEFAULT NULL,
  `Max` float DEFAULT NULL,
  `Unit` varchar(10) DEFAULT NULL,
  `ModelID` int(10) unsigned NOT NULL,
  `PartID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_EquationID_idx` (`ModelID`),
  KEY `FK_Variable_PartID_idx` (`PartID`),
  KEY `FK_Variable_VariableTypeID_idx` (`VarTypeID`),
  CONSTRAINT `FK_Var_ModelID` FOREIGN KEY (`ModelID`) REFERENCES `Model` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_Var_PartID` FOREIGN KEY (`PartID`) REFERENCES `Part` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_Var_VarTypeID` FOREIGN KEY (`VarTypeID`) REFERENCES `VarType` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Var`
--

LOCK TABLES `Var` WRITE;
/*!40000 ALTER TABLE `Var` DISABLE KEYS */;
INSERT INTO `Var` VALUES (1,'pTac',1,0.05,5,'REU',1,8),(2,'pPhlF',3,0,0,'REU',1,23),(3,'pTac',1,0.05,5,'REU',2,8),(4,'pMcbR',3,0,0,'REU',2,28),(5,'pTac',1,0.05,5,'REU',3,8),(6,'pLmrA',3,0,0,'REU',3,33),(7,'pTac',1,0.05,5,'REU',4,8),(8,'pAmeR',3,0,0,'REU',4,38),(9,'pTac',1,0.05,5,'REU',5,8),(10,'pAmtR',3,0,0,'REU',5,43),(11,'pTac',1,0.05,5,'REU',6,8),(12,'pBM3R1',3,0,0,'REU',6,48),(13,'pTac',1,0.05,5,'REU',7,8),(14,'pBetI',3,0,0,'REU',7,53),(15,'pTac',1,0.05,5,'REU',8,8),(16,'pButR',3,0,0,'REU',8,58),(17,'pTac',1,0.05,5,'REU',9,8),(18,'pHapR',3,0,0,'REU',9,63),(19,'pTac',1,0.05,5,'REU',10,8),(20,'pIcaR',3,0,0,'REU',10,68),(21,'pTac',1,0.05,5,'REU',11,8),(22,'pOrf2',3,0,0,'REU',11,73),(23,'pTac',1,0.05,5,'REU',12,8),(24,'pPsrA',3,0,0,'REU',12,78),(25,'pTac',1,0.05,5,'REU',13,8),(26,'pQacR',3,0,0,'REU',13,83),(27,'pTac',1,0.05,5,'REU',14,8),(28,'pScbR',3,0,0,'REU',14,88),(29,'pTac',1,0.05,5,'REU',15,8),(30,'pSmcR',3,0,0,'REU',15,93),(31,'pTac',1,0.05,5,'REU',16,8),(32,'pSrpR',3,0,0,'REU',16,98),(33,'pTac',1,0.05,5,'REU',17,8),(34,'pTarA',3,0,0,'REU',17,103),(35,'pTac',1,0.05,5,'REU',18,8),(36,'pTetR',3,0,0,'REU',18,108),(37,'pTac',1,0.25,50,'REU',19,8),(38,'pPhlF',3,0,0,'REU',19,23),(39,'pTac',1,0.25,50,'REU',20,8),(40,'pBM3R1',3,0,0,'REU',20,48),(41,'pTac',1,0.25,50,'REU',21,8),(42,'pTarA',3,0,0,'REU',21,103),(43,'pTac',1,0.25,50,'REU',22,8),(44,'pSrpR',3,0,0,'REU',22,98),(45,'pTac',1,0.25,50,'REU',23,8),(46,'pHlyIIR',3,0,0,'REU',23,188),(47,'pTac',1,0.25,50,'REU',24,8),(48,'pAmtR',3,0,0,'REU',24,43),(49,'pTac',1,0.25,50,'REU',25,8),(50,'pPhlF',3,0,0,'REU',25,23),(51,'pTac',1,0.25,50,'REU',26,8),(52,'pSrpR',3,0,0,'REU',26,98),(53,'pTac',1,0.25,50,'REU',27,8),(54,'pBM3R1',3,0,0,'REU',27,48),(55,'pTac',1,0.25,50,'REU',28,8),(56,'pQacR',3,0,0,'REU',28,83),(57,'pTac',1,0.25,50,'REU',29,8),(58,'pAmtR',3,0,0,'REU',29,43),(59,'pTac',1,0.25,50,'REU',30,8),(60,'pHlyIIR',3,0,0,'REU',30,188),(61,'pTac',1,0.25,50,'REU',31,8),(62,'pIcaRA',3,0,0,'REU',31,206),(63,'pTac',1,0.25,50,'REU',32,8),(64,'pBetI',3,0,0,'REU',32,53),(65,'pTac',1,0.25,50,'REU',33,8),(66,'pLitR',3,0,0,'REU',33,206),(67,'pTac',1,0.25,50,'REU',34,8),(68,'pPhlF',3,0,0,'REU',34,23),(69,'pTac',1,0.25,50,'REU',35,8),(70,'pSrpR',3,0,0,'REU',35,98),(71,'pTac',1,0.25,50,'REU',36,8),(72,'pBM3R1',3,0,0,'REU',36,48),(73,'pTac',1,0.25,50,'REU',37,8),(74,'pQacR',3,0,0,'REU',37,83),(75,'pTac',1,0.25,50,'REU',38,8),(76,'pSrpR',3,0,0,'REU',38,98),(77,'pTac',1,0.25,50,'REU',39,8),(78,'pSrpR',3,0,0,'REU',39,98);
/*!40000 ALTER TABLE `Var` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `VarType`
--

DROP TABLE IF EXISTS `VarType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VarType` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Type` varchar(20) NOT NULL COMMENT 'Input; Output; Intermediate;',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `VarType`
--

LOCK TABLES `VarType` WRITE;
/*!40000 ALTER TABLE `VarType` DISABLE KEYS */;
INSERT INTO `VarType` VALUES (1,'input'),(2,'intermediate'),(3,'output');
/*!40000 ALTER TABLE `VarType` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-04-01 10:33:21
