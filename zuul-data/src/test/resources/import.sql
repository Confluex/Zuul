insert into Application (id, key, name) values (1, 'FOO','Test Application')

insert into SecurityRole (id, name) values (1, 'ROLE_USER')
insert into SecurityRole (id, name) values (2, 'ROLE_ADMIN')
insert into SecurityRole (id, name) values (3, 'ROLE_SYSTEM_ADMIN')

insert into SecurityUser (id, openId, firstName, lastName, email, enabled) values (1, 'https://www.google.com/accounts/o8/id?id=AItOawnlnuHfoKGwMJSjRHxBROwqil0OE84Zscc', 'Mike', 'Cantrell', 'mike@google.com', true)
insert into SecurityUser (id, openId, firstName, lastName, email, enabled) values (2, 'https://me.yahoo.com/a/mMz2C510uMjhwvHr.4K2aToLWzrPDJb.._M-#b431e', 'Mike', 'Cantrell', 'mike@yahoo.com', true)
