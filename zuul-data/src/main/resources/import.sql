insert into SecurityRole (id, name) values (1, 'ROLE_GUEST')
insert into SecurityRole (id, name) values (2, 'ROLE_USER')
insert into SecurityRole (id, name) values (3, 'ROLE_ADMIN')
insert into SecurityRole (id, name) values (4, 'ROLE_SYSTEM_ADMIN')

-- insert into SecurityUser (id, openId, firstName, lastName, email, enabled) values (1, 'https://www.google.com/accounts/o8/id?id=AItOawnlnuHfoKGwMJSjRHxBROwqil0OE84Zscc', 'Mike', 'Cantrell', 'mike@google.com', true)
insert into SecurityUser (id, openId, firstName, lastName, email, enabled) values (2, 'https://me.yahoo.com/a/mMz2C510uMjhwvHr.4K2aToLWzrPDJb.._M-#b431e', 'Mike', 'Cantrell', 'mike@yahoo.com', true)

insert into SecurityUserRole(userId, roleId) values (2, 4)

-- =========== Settings Groups ===========
insert into SettingsGroup (id, name, environment) values (1, 'app-data-config', 'dev')
insert into SettingsGroup (id, name, environment) values (2, 'app-data-config', 'qa')
insert into SettingsGroup (id, name, environment) values (3, 'app-data-config', 'prod')

-- =========== Settings Entries ===========
insert into SettingsEntry(id, groupId, key, value) values (1, 1, 'jdbc.zuul.url', 'jdbc:h2:mem:zuul')
insert into SettingsEntry(id, groupId, key, value) values (2, 1, 'jdbc.zuul.generate.ddl', 'create-drop')
insert into SettingsEntry(id, groupId, key, value) values (3, 1, 'jdbc.zuul.username', 'sa')
insert into SettingsEntry(id, groupId, key, value) values (4, 1, 'jdbc.zuul.password', '')
insert into SettingsEntry(id, groupId, key, value) values (5, 1, 'jdbc.zuul.driver', 'org.h2.Driver')
insert into SettingsEntry(id, groupId, key, value) values (6, 1, 'jdbc.zuul.dialect', 'org.hibernate.dialect.H2Dialect')
insert into SettingsEntry(id, groupId, key, value) values (7, 1, 'jdbc.zuul.validationQuery', 'select 1')