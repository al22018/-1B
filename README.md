"# -1B"
Ogino Arata created the DB as follows:
CREATE TABLE UsersTableNinth(
UserID INTEGER PRIMARY KEY,
DisplayName VARCHAR(50),
AuthToken VARCHAR(300),
Email VARCHAR(50),
CalendarInfo TEXT
);

CREATE TABLE ProjectsTableNinth(
ProjectID INTEGER PRIMARY KEY,
Name VARCHAR(50),
DateTime TIMESTAMP,
Category VARCHAR(50),
Destination VARCHAR(50),
ManagerID INTEGER,
Region VARCHAR(50),
ProgressStatus VARCHAR(50),
FOREIGN KEY (ManagerID) REFERENCES UsersTableNinth(UserID)
);

CREATE TABLE UserAndProjectsDetailsTableNinth(
UserID INTEGER,
ProjectID INTEGER,
Genre VARCHAR(50),
Budget1 VARCHAR(50),
Budget2 VARCHAR(50),
Review VARCHAR(50),
PRIMARY KEY(UserID, ProjectID),
FOREIGN KEY (UserID) REFERENCES UsersTableNinth(UserID),
FOREIGN KEY (ProjectID) REFERENCES ProjectsTableNinth(ProjectID)
);
How to run SearchEvents.java
compile:
javac -cp ".;..\lib\jsoup-1.17.2.jar;..\lib\jackson-core-2.13.3.jar;..\lib\jackson-annotations-2.13.3.jar;..\lib\jackson-databind-2.13.3.jar" SearchEvents.java Event.java
run:
java -cp ".;..\lib\jsoup-1.17.2.jar;..\lib\jackson-core-2.13.3.jar;..\lib\jackson-annotations-2.13.3.jar;..\lib\jackson-databind-2.13.3.jar" SearchEvents
