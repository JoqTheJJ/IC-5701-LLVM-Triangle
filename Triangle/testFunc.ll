define i32 @identidad() {
  ret i32 null
}

declare void @printChar(i8)
declare i8 @readChar()
declare void @printInt(i32)
declare i32 @readInt()
@true = global i32 1
@false = global i32 0
define i32 @main() {
  %t0 = call i32 @identidad()
  call void @printInt(i32 %t0)
  ret i32 0
}
