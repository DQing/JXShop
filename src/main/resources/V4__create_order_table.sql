CREATE TABLE `order` (
  id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  userId int(10),
  totalPrice int(10),
  createTime TIMESTAMP NULL DEFAULT NULL,
  paidTime TIMESTAMP NULL DEFAULT NULL,
  withdrawnTime TIMESTAMP NULL DEFAULT NULL,
  finishTime TIMESTAMP NULL DEFAULT NULL,
  status ENUM('unPaid','paid','withdrawn','finished'),
  logisticsRecordId int(10),
  FOREIGN KEY (logisticsRecordId) REFERENCES logisticsRecord(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
