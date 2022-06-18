use employees;
DELIMITER ||
create function searchEmployee (name1 varchar(20), name2 varchar(20))
returns varchar(100) deterministic
begin
declare return_value varchar(100);
declare return_value1 varchar(100);
declare return_value2 varchar(100);
declare return_value3 varchar(100);
declare return_value4 varchar(100);
declare return_value5 varchar(100);
set return_value = (select emp_no from employees where first_name = name1 and last_name = name2);
set return_value1 = (select birth_date from employees where first_name = name1 and last_name = name2);
set return_value2 = (select first_name from employees where first_name = name1 and last_name = name2);
set return_value3 = (select last_name from employees where first_name = name1 and last_name = name2);
set return_value4 = (select gender from employees where first_name = name1 and last_name = name2);
set return_value5 = (select hire_date from employees where first_name = name1 and last_name = name2);
return concat(return_value,' ',return_value1,' ',return_value2,' ',return_value3,' ',return_value4,' ',return_value5);
end;
||
DELIMITER ;

select searchEmployee("Tzvetan", "Zielinski");
