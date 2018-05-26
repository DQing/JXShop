CREATE TABLE logisticsRecord(
  id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  logisticsStatus ENUM('readyToShip','shipping','signed'),
  outboundTime TIMESTAMP NULL DEFAULT NULL,
  signedTime TIMESTAMP NULL DEFAULT NULL,
  deliveryMan VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;