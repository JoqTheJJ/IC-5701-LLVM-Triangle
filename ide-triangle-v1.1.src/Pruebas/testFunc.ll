@true = global i32 1
@false = global i32 0
declare i32 @readInt()
declare void @printInt(i32)
declare i8 @readChar()
declare void @printChar(i8)
define i32 @identidad(i32 %n) {
  %n.addr = alloca i32
  store i32 %n, ptr %n.addr
  %t0 = load i32, ptr %n.addr
  ret i32 %t0
}
define i32 @main() {
  %t1 = add i32 59, 0
  %t2 = call i32 @identidad(i32 %t1)
  call void @printInt(i32 %t2)
  ret i32 0
}
