@x = global i32 0
declare void @printChar(i8)
declare i8 @readChar()
declare void @printInt(i32)
declare i32 @readInt()
define i32 @main() {
  %t0 = add i32 7, 0
  store i32 %t0, ptr @x
  %t4 = load i32, ptr @x
  %t5 = add i32 5, 0
  %t6 = icmp sgt i32 %t4, %t5
  %t7 = zext i1 %t6 to i32
  %t8 = icmp ne i32 %t7, 0
  br i1 %t8, label %then1, label %else2
then1:
  %t9 = add i32 1, 0
  call void @printInt(i32 %t9)
  br label %endif3
else2:
  %t10 = add i32 0, 0
  call void @printInt(i32 %t10)
  br label %endif3
endif3:
  ret i32 0
}
