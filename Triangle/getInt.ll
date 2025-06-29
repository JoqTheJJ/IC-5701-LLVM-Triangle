@x = global i32 0
declare void @printChar(i8)
declare i8 @readChar()
declare void @printInt(i32)
declare i32 @readInt()
@true = global i32 1
@false = global i32 0
define i32 @main() {
  %t0 = call i32 @readInt()
  store i32 %t0, ptr @x
  %t1 = load i32, ptr @x
  call void @printInt(i32 %t1)
  ret i32 0
}
