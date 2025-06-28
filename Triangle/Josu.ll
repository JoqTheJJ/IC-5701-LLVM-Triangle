@x = global i32 0
declare void @printChar(i8)
declare i8 @readChar()
declare void @printInt(i32)
declare i32 @readInt()
define i32 @main() {
  %t0 = add i32 5, 0
  store i32 %t0, ptr @x
  %t1 = add i32 4, 0
  %t2 = add i32 2, 0
  %t3 = add i32 %t1, %t2
  store i32 %t3, ptr @x
  %t4 = add i32 10, 0
  %t5 = load i32, ptr @x
  %t6 = add i32 %t4, %t5
  store i32 %t6, ptr @x
  %t7 = load i32, ptr @x
  call void @printInt(i32 %t7)
  ret i32 0
}
