********** Triangle Compiler (Java Version 2.1) Hola Mundo! **********
Syntactic Analysis ...
Contextual Analysis ...
LLVM Code Generation ...
===== CËDIGO LLVM =====
@n = global i32 0
declare void @printInt(i32)
declare i32 @readInt()
define i32 @main() {
  %t0 = call i32 @readInt()
  store i32 %t0, ptr @n
  call void @printInt(i32 null)
  ret i32 0
}

Compilation was successful.
