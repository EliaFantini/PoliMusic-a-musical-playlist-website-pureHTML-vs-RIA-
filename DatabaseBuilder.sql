CREATE DATABASE  IF NOT EXISTS `TIW_DB` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `TIW_DB`;
-- MySQL dump 10.13  Distrib 8.0.15, for macos10.14 (x86_64)
--
-- Host: localhost    Database: TIW_DB
-- ------------------------------------------------------
-- Server version	8.0.23

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `album`
--

DROP TABLE IF EXISTS `album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `album` (
  `album_ID` int NOT NULL AUTO_INCREMENT,
  `album_title` varchar(45) NOT NULL,
  `interpreter` varchar(45) NOT NULL,
  `publication_year` int NOT NULL,
  `image_path` varchar(200) DEFAULT NULL,
  `user_ID` int NOT NULL,
  PRIMARY KEY (`album_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
INSERT INTO `album` VALUES (17,'Evolve','ImagineDragons',2017,'uploads/1/Evolve.jpg',1),(18,'Immigrant Song','Led Zeppelin',1970,'uploads/1/ImmigrandSong.jpg',1),(19,'Bohemian Rhapsody','Queen',1975,'uploads/1/BohemianRhapsody.jpg',1),(20,'Razors Edge','ACDC',1990,'uploads/1/TheRazorsEdge.jpg',1),(21,'Californication','Red Hot Chili Peppers',1999,'uploads/1/californication.jpg',1),(22,'Blurryface','Twenty One Pilots',2015,'uploads/1/Blurryface.jpg',1);
/*!40000 ALTER TABLE `album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `containment`
--

DROP TABLE IF EXISTS `containment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `containment` (
  `containment_ID` int NOT NULL AUTO_INCREMENT,
  `song_ID` int NOT NULL,
  `playlist_ID` int NOT NULL,
  `ordinal` int NOT NULL,
  PRIMARY KEY (`containment_ID`),
  KEY `song_ID_idx` (`song_ID`),
  KEY `playlist_ID_idx` (`playlist_ID`),
  CONSTRAINT `playlist_ID` FOREIGN KEY (`playlist_ID`) REFERENCES `playlist` (`playlist_ID`),
  CONSTRAINT `song_ID` FOREIGN KEY (`song_ID`) REFERENCES `song` (`song_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `containment`
--

LOCK TABLES `containment` WRITE;
/*!40000 ALTER TABLE `containment` DISABLE KEYS */;
INSERT INTO `containment` VALUES (21,19,5,2),(22,20,5,4),(23,21,5,3),(27,23,5,6),(30,22,5,1990),(38,33,5,1999);
/*!40000 ALTER TABLE `containment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `playlist` (
  `playlist_ID` int NOT NULL AUTO_INCREMENT,
  `playlist_name` varchar(45) NOT NULL,
  `creator_ID` int NOT NULL,
  `creation_date` datetime NOT NULL,
  PRIMARY KEY (`playlist_ID`),
  KEY `creator_idx` (`creator_ID`),
  CONSTRAINT `creator` FOREIGN KEY (`creator_ID`) REFERENCES `user` (`user_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` VALUES (5,'New Songs',1,'2021-05-31 00:00:00'),(6,'Game of Songs',2,'2021-06-03 00:00:00'),(7,'Beautiful Songs',1,'2021-06-15 00:00:00');
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `song`
--

DROP TABLE IF EXISTS `song`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `song` (
  `song_ID` int NOT NULL AUTO_INCREMENT,
  `song_title` varchar(45) NOT NULL,
  `album_ID` int NOT NULL,
  `genre` varchar(45) NOT NULL,
  `file_path` varchar(200) DEFAULT NULL,
  `owner_ID` int NOT NULL,
  PRIMARY KEY (`song_ID`),
  KEY `album_idx` (`album_ID`),
  KEY `owner_ID_idx` (`owner_ID`),
  CONSTRAINT `album` FOREIGN KEY (`album_ID`) REFERENCES `album` (`album_ID`),
  CONSTRAINT `owner_ID` FOREIGN KEY (`owner_ID`) REFERENCES `user` (`user_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `song`
--

LOCK TABLES `song` WRITE;
/*!40000 ALTER TABLE `song` DISABLE KEYS */;
INSERT INTO `song` VALUES (19,'Believer',17,'Rock','uploads/1/Believer.mp3',1),(20,'Immigrant Song',18,'Rock','uploads/1/ImmigrantSong.mp3',1),(21,'Bohemian Rhapsody',19,'Rock','uploads/1/BohemianRhapsody.mp3',1),(22,'Thunderstruck',20,'Rock','uploads/1/Thunderstruck.mp3',1),(23,'Whatever It Takes',17,'Rock','uploads/1/WhateverItTakes.mp3',1),(33,'Californication',21,'Rock','uploads/1/Californication.mp3',1);
/*!40000 ALTER TABLE `song` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user` (
  `user_ID` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`user_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Harry','Potter'),(2,'Jon','Snow');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-06-16 18:38:14
