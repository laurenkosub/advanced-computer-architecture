; ModuleID = './c/test_disp.c'
target datalayout = "E-S32-p:32:32:32-i8:8:8-i16:16:16-i32:32:32-i64:32:32-f64:32:32-a0:0:32-s0:32:32-v64:32:32-v128:32:32-n32"
target triple = "patmos-unknown-unknown-elf"

@.str = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str1 = private unnamed_addr constant [28 x i8] c"Testing tuning fork display\00", align 1
@.str2 = private unnamed_addr constant [32 x i8] c"\0AEnter a number to be display:\0A\00", align 1
@.str3 = private unnamed_addr constant [3 x i8] c"%d\00", align 1

; Function Attrs: nounwind
define void @resetDisp(i32 %disp_addr, i32 %from, i32 %to) #0 {
entry:
  %0 = alloca i32, align 4
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %disp_ptr = alloca i32 addrspace(1)*, align 4
  %pos = alloca i32, align 4
  store i32 %disp_addr, i32* %0, align 4
  store i32 %from, i32* %1, align 4
  store i32 %to, i32* %2, align 4
  %3 = load i32* %0, align 4
  %4 = inttoptr i32 %3 to i32 addrspace(1)*
  store i32 addrspace(1)* %4, i32 addrspace(1)** %disp_ptr, align 4
  store i32 0, i32* %pos, align 4
  %5 = load i32* %1, align 4
  store i32 %5, i32* %pos, align 4
  br label %for.cond

for.cond:                                         ; preds = %for.inc, %entry
  %6 = load i32* %pos, align 4
  %7 = load i32* %2, align 4
  %8 = icmp ult i32 %6, %7
  br i1 %8, label %for.body, label %for.end

for.body:                                         ; preds = %for.cond
  %9 = load i32 addrspace(1)** %disp_ptr, align 4
  store volatile i32 255, i32 addrspace(1)* %9, align 4
  %10 = load i32 addrspace(1)** %disp_ptr, align 4
  %11 = getelementptr inbounds i32 addrspace(1)* %10, i32 1
  store i32 addrspace(1)* %11, i32 addrspace(1)** %disp_ptr, align 4
  br label %for.inc

for.inc:                                          ; preds = %for.body
  %12 = load i32* %pos, align 4
  %13 = add i32 %12, 1
  store i32 %13, i32* %pos, align 4
  br label %for.cond

for.end:                                          ; preds = %for.cond
  ret void
}

; Function Attrs: nounwind
define void @inputDisp(i32 %disp_addr, i32 %from, i32 %to) #0 {
entry:
  %0 = alloca i32, align 4
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %disp_ptr = alloca i32 addrspace(1)*, align 4
  %pos = alloca i32, align 4
  store i32 %disp_addr, i32* %0, align 4
  store i32 %from, i32* %1, align 4
  store i32 %to, i32* %2, align 4
  %3 = load i32* %0, align 4
  %4 = inttoptr i32 %3 to i32 addrspace(1)*
  store i32 addrspace(1)* %4, i32 addrspace(1)** %disp_ptr, align 4
  store i32 0, i32* %pos, align 4
  %5 = load i32* %1, align 4
  store i32 %5, i32* %pos, align 4
  br label %for.cond

for.cond:                                         ; preds = %for.inc, %entry
  %6 = load i32* %pos, align 4
  %7 = load i32* %2, align 4
  %8 = icmp ult i32 %6, %7
  br i1 %8, label %for.body, label %for.end

for.body:                                         ; preds = %for.cond
  %9 = load i32 addrspace(1)** %disp_ptr, align 4
  store volatile i32 247, i32 addrspace(1)* %9, align 4
  %10 = load i32 addrspace(1)** %disp_ptr, align 4
  %11 = getelementptr inbounds i32 addrspace(1)* %10, i32 1
  store i32 addrspace(1)* %11, i32 addrspace(1)** %disp_ptr, align 4
  br label %for.inc

for.inc:                                          ; preds = %for.body
  %12 = load i32* %pos, align 4
  %13 = add i32 %12, 1
  store i32 %13, i32* %pos, align 4
  br label %for.cond

for.end:                                          ; preds = %for.cond
  ret void
}

; Function Attrs: noinline nounwind
define void @printSegmentInt(i32 %base_addr, i32 %number, i32 %displayCount) #1 {
entry:
  %0 = alloca i32, align 4
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %disp_ptr = alloca i32 addrspace(1)*, align 4
  %pos = alloca i32, align 4
  %byte_mask = alloca i32, align 4
  %range = alloca i32, align 4
  %value = alloca i32, align 4
  store i32 %base_addr, i32* %0, align 4
  store i32 %number, i32* %1, align 4
  store i32 %displayCount, i32* %2, align 4
  %3 = load i32* %0, align 4
  %4 = inttoptr i32 %3 to i32 addrspace(1)*
  store i32 addrspace(1)* %4, i32 addrspace(1)** %disp_ptr, align 4
  store i32 0, i32* %pos, align 4
  store i32 15, i32* %byte_mask, align 4
  %5 = load i32* %1, align 4
  %6 = icmp sgt i32 %5, 0
  br i1 %6, label %cond.true, label %cond.false

cond.true:                                        ; preds = %entry
  %7 = load i32* %2, align 4
  br label %cond.end

cond.false:                                       ; preds = %entry
  %8 = load i32* %2, align 4
  %9 = sub nsw i32 %8, 1
  br label %cond.end

cond.end:                                         ; preds = %cond.false, %cond.true
  %10 = phi i32 [ %7, %cond.true ], [ %9, %cond.false ]
  store i32 %10, i32* %range, align 4
  %11 = load i32* %1, align 4
  %12 = call i32 @abs(i32 %11) #4
  store i32 %12, i32* %value, align 4
  store i32 0, i32* %pos, align 4
  br label %for.cond

for.cond:                                         ; preds = %for.inc, %cond.end
  %13 = load i32* %pos, align 4
  %14 = load i32* %range, align 4
  %15 = icmp ult i32 %13, %14
  br i1 %15, label %for.body, label %for.end

for.body:                                         ; preds = %for.cond
  %16 = load i32* %value, align 4
  %17 = load i32* %byte_mask, align 4
  %18 = and i32 %16, %17
  %19 = load i32* %pos, align 4
  %20 = mul i32 %19, 4
  %21 = lshr i32 %18, %20
  %22 = load i32 addrspace(1)** %disp_ptr, align 4
  store volatile i32 %21, i32 addrspace(1)* %22, align 4
  %23 = load i32* %byte_mask, align 4
  %24 = shl i32 %23, 4
  store i32 %24, i32* %byte_mask, align 4
  %25 = load i32 addrspace(1)** %disp_ptr, align 4
  %26 = getelementptr inbounds i32 addrspace(1)* %25, i32 1
  store i32 addrspace(1)* %26, i32 addrspace(1)** %disp_ptr, align 4
  br label %for.inc

for.inc:                                          ; preds = %for.body
  %27 = load i32* %pos, align 4
  %28 = add i32 %27, 1
  store i32 %28, i32* %pos, align 4
  br label %for.cond

for.end:                                          ; preds = %for.cond
  %29 = load i32* %1, align 4
  %30 = icmp slt i32 %29, 0
  br i1 %30, label %if.then, label %if.end

if.then:                                          ; preds = %for.end
  %31 = load i32 addrspace(1)** %disp_ptr, align 4
  store volatile i32 191, i32 addrspace(1)* %31, align 4
  br label %if.end

if.end:                                           ; preds = %if.then, %for.end
  ret void
}

; Function Attrs: nounwind readnone
declare i32 @abs(i32) #2

; Function Attrs: nounwind
define i32 @main(i32 %argc, i8** %argv) #0 {
entry:
  %0 = alloca i32, align 4
  %1 = alloca i32, align 4
  %2 = alloca i8**, align 4
  %uart_ptr = alloca i32 addrspace(1)*, align 4
  %led_ptr = alloca i32 addrspace(1)*, align 4
  %disp_ptr = alloca i32 addrspace(1)*, align 4
  %intro = alloca i32, align 4
  %i = alloca i32, align 4
  %j = alloca i32, align 4
  %i17 = alloca i32, align 4
  %x = alloca i32, align 4
  store i32 0, i32* %0
  store i32 %argc, i32* %1, align 4
  store i8** %argv, i8*** %2, align 4
  store i32 addrspace(1)* inttoptr (i32 -267911168 to i32 addrspace(1)*), i32 addrspace(1)** %uart_ptr, align 4
  store i32 addrspace(1)* inttoptr (i32 -267845632 to i32 addrspace(1)*), i32 addrspace(1)** %led_ptr, align 4
  store i32 addrspace(1)* inttoptr (i32 -268107776 to i32 addrspace(1)*), i32 addrspace(1)** %disp_ptr, align 4
  %3 = call i32 @puts(i8* getelementptr inbounds ([2 x i8]* @.str, i32 0, i32 0))
  %4 = call i32 @puts(i8* getelementptr inbounds ([28 x i8]* @.str1, i32 0, i32 0))
  call void @inputDisp(i32 -268107776, i32 0, i32 8)
  store i32 16, i32* %intro, align 4
  br label %for.cond

for.cond:                                         ; preds = %for.inc15, %entry
  %5 = load i32* %intro, align 4
  %6 = icmp ne i32 %5, 0
  br i1 %6, label %for.body, label %for.end16

for.body:                                         ; preds = %for.cond
  %7 = load i32 addrspace(1)** %uart_ptr, align 4
  store volatile i32 49, i32 addrspace(1)* %7, align 4
  store i32 1024, i32* %i, align 4
  br label %for.cond1

for.cond1:                                        ; preds = %for.inc5, %for.body
  %8 = load i32* %i, align 4
  %9 = icmp ne i32 %8, 0
  br i1 %9, label %for.body2, label %for.end6

for.body2:                                        ; preds = %for.cond1
  store i32 1024, i32* %j, align 4
  br label %for.cond3

for.cond3:                                        ; preds = %for.inc, %for.body2
  %10 = load i32* %j, align 4
  %11 = icmp ne i32 %10, 0
  br i1 %11, label %for.body4, label %for.end

for.body4:                                        ; preds = %for.cond3
  %12 = load i32 addrspace(1)** %led_ptr, align 4
  store volatile i32 1, i32 addrspace(1)* %12, align 4
  br label %for.inc

for.inc:                                          ; preds = %for.body4
  %13 = load i32* %j, align 4
  %14 = add nsw i32 %13, -1
  store i32 %14, i32* %j, align 4
  br label %for.cond3

for.end:                                          ; preds = %for.cond3
  br label %for.inc5

for.inc5:                                         ; preds = %for.end
  %15 = load i32* %i, align 4
  %16 = add nsw i32 %15, -1
  store i32 %16, i32* %i, align 4
  br label %for.cond1

for.end6:                                         ; preds = %for.cond1
  %17 = load i32 addrspace(1)** %uart_ptr, align 4
  store volatile i32 48, i32 addrspace(1)* %17, align 4
  store i32 1024, i32* %i, align 4
  br label %for.cond7

for.cond7:                                        ; preds = %for.inc13, %for.end6
  %18 = load i32* %i, align 4
  %19 = icmp ne i32 %18, 0
  br i1 %19, label %for.body8, label %for.end14

for.body8:                                        ; preds = %for.cond7
  store i32 1024, i32* %j, align 4
  br label %for.cond9

for.cond9:                                        ; preds = %for.inc11, %for.body8
  %20 = load i32* %j, align 4
  %21 = icmp ne i32 %20, 0
  br i1 %21, label %for.body10, label %for.end12

for.body10:                                       ; preds = %for.cond9
  %22 = load i32 addrspace(1)** %led_ptr, align 4
  store volatile i32 0, i32 addrspace(1)* %22, align 4
  br label %for.inc11

for.inc11:                                        ; preds = %for.body10
  %23 = load i32* %j, align 4
  %24 = add nsw i32 %23, -1
  store i32 %24, i32* %j, align 4
  br label %for.cond9

for.end12:                                        ; preds = %for.cond9
  br label %for.inc13

for.inc13:                                        ; preds = %for.end12
  %25 = load i32* %i, align 4
  %26 = add nsw i32 %25, -1
  store i32 %26, i32* %i, align 4
  br label %for.cond7

for.end14:                                        ; preds = %for.cond7
  br label %for.inc15

for.inc15:                                        ; preds = %for.end14
  %27 = load i32* %intro, align 4
  %28 = add nsw i32 %27, -1
  store i32 %28, i32* %intro, align 4
  br label %for.cond

for.end16:                                        ; preds = %for.cond
  store i32 0, i32* %i17, align 4
  br label %for.cond18

for.cond18:                                       ; preds = %for.inc20, %for.end16
  %29 = load i32* %i17, align 4
  %30 = icmp slt i32 %29, 8
  br i1 %30, label %for.body19, label %for.end21

for.body19:                                       ; preds = %for.cond18
  %31 = load i32* %i17, align 4
  %32 = load i32* %i17, align 4
  %33 = load i32 addrspace(1)** %disp_ptr, align 4
  %34 = getelementptr inbounds i32 addrspace(1)* %33, i32 %32
  store volatile i32 %31, i32 addrspace(1)* %34, align 4
  br label %for.inc20

for.inc20:                                        ; preds = %for.body19
  %35 = load i32* %i17, align 4
  %36 = add nsw i32 %35, 1
  store i32 %36, i32* %i17, align 4
  br label %for.cond18

for.end21:                                        ; preds = %for.cond18
  store i32 0, i32* %x, align 4
  %37 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([32 x i8]* @.str2, i32 0, i32 0))
  %38 = call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([3 x i8]* @.str3, i32 0, i32 0), i32* %x)
  call void @resetDisp(i32 -268107776, i32 0, i32 8)
  %39 = load i32* %x, align 4
  call void @printSegmentInt(i32 -268107776, i32 %39, i32 8)
  ret i32 0
}

declare i32 @puts(i8*) #3

declare i32 @printf(i8*, ...) #3

declare i32 @scanf(i8*, ...) #3

attributes #0 = { nounwind "less-precise-fpmad"="false" "no-frame-pointer-elim"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "unsafe-fp-math"="false" "use-soft-float"="true" }
attributes #1 = { noinline nounwind "less-precise-fpmad"="false" "no-frame-pointer-elim"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "unsafe-fp-math"="false" "use-soft-float"="true" }
attributes #2 = { nounwind readnone "less-precise-fpmad"="false" "no-frame-pointer-elim"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "unsafe-fp-math"="false" "use-soft-float"="true" }
attributes #3 = { "less-precise-fpmad"="false" "no-frame-pointer-elim"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "unsafe-fp-math"="false" "use-soft-float"="true" }
attributes #4 = { nounwind readnone }

!llvm.ident = !{!0}

!0 = metadata !{metadata !"clang version 3.4 "}
