@x = global i32 0
@y = global i32 0
declare void @printChar(i8)
declare i8 @readChar()
declare void @printInt(i32)
declare i32 @readInt()
define i32 @main() {
  %t0 = load i32, ptr @x
  %t1 = add i32 5, 0
  %t2 = add i32 %t0, %t1
  store i32 %t2, ptr @y
  ret i32 0
}
