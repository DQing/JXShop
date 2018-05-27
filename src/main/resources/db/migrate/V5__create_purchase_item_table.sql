CREATE TABLE purchaseItem(
  id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  productName varchar(1000),
  productDescription varchar(1000),
  purchaseCount int(10),
  purchasePrice int(10),
  orderId int(10),
  productId int(10),
  FOREIGN KEY (orderId) REFERENCES `order`(id),
  FOREIGN KEY (productId) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;