@n = global i32 0
declare void @printChar(i8)
declare i8 @readChar()
declare void @printInt(i32)
declare i32 @readInt()
define i32 @main() {
  %t0 = add i32 97, 0
  store i32 %t0, ptr @n
  %t1 = load i32, ptr @n
  %t2 = trunc i32 %t1 to i8
  call void @printChar(i8 %t2)
  ret i32 0
}
