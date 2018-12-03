create table dezhou_user (
	uid int(11) auto_increment,
	account varchar(50) default '',
	password varchar(20) default '',
	roommoney int(11) default 0,
	allmoney int(11) default 100000,
	exprience int default 0,
	gold int default 0,
	mobile varchar(20),
	level int default 0,
	sex varchar(10) default 'male',
	address varchar(50) default '',
	regtime varchar(30) default '',
	birthday varchar(30) default '',
	logintime varchar(20) default '',
	primary key(uid),
	UNIQUE KEY uidx_account (account)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;