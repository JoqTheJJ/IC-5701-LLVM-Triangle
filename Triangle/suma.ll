@resultado = global i32 0
@b = global i32 0
@a = global i32 0
declare void @printChar(i8)
declare i8 @readChar()
declare void @printInt(i32)
declare i32 @readInt()
@true = global i32 1
@false = global i32 0
define i32 @main() {
  %t0 = call i32 @readInt()
  store i32 %t0, ptr @a
  %t1 = call i32 @readInt()
  store i32 %t1, ptr @b
  %t2 = load i32, ptr @a
  %t3 = load i32, ptr @b
  %t4 = add i32 %t2, %t3
  store i32 %t4, ptr @resultado
  %t5 = load i32, ptr @resultado
  call void @printInt(i32 %t5)
  ret i32 0
}
