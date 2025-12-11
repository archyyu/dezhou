
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

create table dezhou_room (
	id int(11) auto_increment,
	showname varchar(50) default '',
	name varchar(50) default '',
	bbet int(11) default 0,
	sbet int(11) default 0,
	maxbuy int(11) default 0,
	minbuy int(11) default 0,
	roomtype varchar(10) default '',
	primary key(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

// generate some rooms please run below sqls
insert into dezhou_room (showname, name, bbet, sbet, maxbuy, minbuy, roomtype) values ('初级场', 'beginner', 20, 10, 2000, 200, 'public');
insert into dezhou_room (showname, name, bbet, sbet, maxbuy, minbuy, roomtype) values ('中级场', 'intermediate', 200, 100, 20000, 2000, 'public');
insert into dezhou_room (showname, name, bbet, sbet, maxbuy, minbuy, roomtype) values ('高级场', 'advanced', 2000, 1000, 200000, 20000, 'public');
insert into dezhou_room (showname, name, bbet, sbet, maxbuy, minbuy, roomtype) values ('土豪场', 'tycoon', 20000, 10000, 2000000, 200000, 'public');