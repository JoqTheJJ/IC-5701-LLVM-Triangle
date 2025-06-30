@true = global i32 1
@false = global i32 0
declare i32 @readInt()
declare void @printInt(i32)
declare i8 @readChar()
declare void @printChar(i8)
@x = global i32 0
define i32 @main() {
  %t0 = add i32 0, 0
  store i32 %t0, ptr @x
  br label %loop1
loop1:
  %t4 = load i32, ptr @x
  %t5 = add i32 5, 0
  %t6 = icmp slt i32 %t4, %t5
  br i1 %t6, label %body2, label %end3
body2:
  %t7 = load i32, ptr @x
  call void @printInt(i32 %t7)
  %t8 = load i32, ptr @x
  %t9 = add i32 1, 0
  %t10 = add i32 %t8, %t9
  store i32 %t10, ptr @x
  br label %loop1
end3:
  %t11 = add i32 70, 0
  %t12 = trunc i32 %t11 to i8
  call void @printChar(i8 %t12)
  ret i32 0
}
