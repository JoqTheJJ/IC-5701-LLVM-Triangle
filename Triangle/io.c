// io.c
#include <stdio.h>

int readInt() {
    int n;
    scanf("%d", &n);
    return n;
}

void printInt(int n) {
    printf("%d\n", n);
}

char readChar() {
    char c;
    scanf(" %c", &c); // Espacio ignora saltos de línea previos
    return c;
}

void printChar(char c) {
    printf("%c", c);
}
