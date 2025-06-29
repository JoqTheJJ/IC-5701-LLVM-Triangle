@n = global i32 0
declare void @printChar(i8)
declare i8 @readChar()
declare void @printInt(i32)
declare i32 @readInt()
@true = global i32 1
@false = global i32 0
define i32 @main() {
  %t0 = add i32 42, 0
  store i32 %t0, ptr @n
  %t1 = load i32, ptr @n
  call void @printInt(i32 %t1)
  ret i32 0
}
