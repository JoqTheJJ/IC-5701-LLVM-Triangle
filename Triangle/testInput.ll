@c = global i32 0
declare void @printChar(i8)
declare i8 @readChar()
declare void @printInt(i32)
declare i32 @readInt()
@true = global i32 1
@false = global i32 0
define i32 @main() {
  %t0 = call i8 @readChar()
  %t1 = zext i8 %t0 to i32
  store i32 %t1, ptr @c
  %t2 = load i32, ptr @c
  %t3 = trunc i32 %t2 to i8
  call void @printChar(i8 %t3)
  ret i32 0
}
