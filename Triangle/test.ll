@x = global i32 0
declare i32 @readInt()
declare void @printInt(i32)
define i32 @main() {
  %t0 = add i32 42, 0
  store i32 %t0, ptr @x
  ret i32 0
}