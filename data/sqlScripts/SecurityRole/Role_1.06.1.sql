BEGIN
   FOR X IN 10000 .. 10120
   LOOP
   
        Insert into CSM_ROLE (ROLE_ID, ROLE_NAME, ENTERPRISE_NAME, DESCRIPTION, INHERITABLE) Values (X, 'ROLE_1.06.1-' || X, 'JDA', 'This is text Role', 0);

   END LOOP;
END;
/
COMMIT;
Exit;
