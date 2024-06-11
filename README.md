"# -1B"
Ogino Arata created the DB as follows:
{
CREATE TABLE UsersTableNinth(
UserID INTEGER PRIMARY KEY,
DisplayName VARCHAR(50),
AuthToken VARCHAR(300),
Email VARCHAR(50),
CalendarInfo VARCHAR(50));

CREATE TABLE ProjectsTableNinth(
ProjectID INTEGER PRIMARY KEY,
Name VARCHAR(50),
DateTime TIMESTAMP,
Category VARCHAR(50),
Destination VARCHAR(50),
ManagerID INTEGER,
Region VARCHAR(50),
FOREIGN KEY (ManagerID) REFERENCES UsersTableNinth (UserID));

CREATE TABLE UserAndProjectsDetailsTableNinth(
UserID INTEGER,
ProjectID INTEGER,
VoteContent VARCHAR(50),
ProgressStatus VARCHAR(50),
FOREIGN KEY (UserID) REFERENCES UsersTableNinth (UserID),
FOREIGN KEY (ProjectID) REFERENCES ProjectsTableNinth (ProjectID),
PRIMARY KEY(UserID, ProjectID));
}
