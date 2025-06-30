@true = global i32 1
@false = global i32 0
declare i32 @readInt()
declare void @printInt(i32)
declare i8 @readChar()
declare void @printChar(i8)
@flag = global i32 0
define i32 @main() {
  %t0 = load i32, ptr @false
  store i32 %t0, ptr @flag
  %t4 = load i32, ptr @flag
  %t5 = icmp ne i32 %t4, 0
  br i1 %t5, label %then1, label %else2
then1:
  %t6 = add i32 1, 0
  call void @printInt(i32 %t6)
  br label %endif3
else2:
  %t7 = add i32 0, 0
  call void @printInt(i32 %t7)
  br label %endif3
endif3:
  ret i32 0
}
