CREATE TABLE product(
  id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name varchar(1000),
  description varchar(1000),
  price int(10),
  inventoryId int(10) DEFAULT NULL,
  FOREIGN KEY (inventoryId) REFERENCES inventory(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
