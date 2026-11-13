INSERT INTO sys_user (username, display_name, password_hash, role, status)
VALUES ('admin', '系统管理员',
        'pbkdf2$120000$bNCyhBgsW5d/vmVndLMeeQ==$pxeuzpoO6lDTKZ+2z2ep/gFpKkLt9a5G114S8294wB8=',
        'ADMIN', 1)
ON DUPLICATE KEY UPDATE display_name = '系统管理员', status = 1;

INSERT INTO sys_user (username, display_name, password_hash, role, status)
VALUES ('customer', '演示顾客',
        'pbkdf2$120000$AH5tLMiHh74zeNcVB08F0Q==$e7vW6TVT/fgh4oFfcediEbn0760LszwwnzYnaFVThlM=',
        'USER', 1)
ON DUPLICATE KEY UPDATE display_name = '演示顾客', status = 1;
