(define (problem pfile0)
	(:domain cushing)
	(:objects
		var1 var2 var3 var4 var5 var6 var7 var8 var9 var10 - variable
	)
	(:init
		(norepeat var1)
		(norepeat var2)
		(norepeat var3)
		(norepeat var4)
		(norepeat var5)
		(norepeat var6)
		(norepeat var7)
		(norepeat var8)
		(norepeat var9)
		(norepeat var10)
	)
	(:goal
		(and
			(target1 var1)
			(target2 var1)
			(target3 var1)
			(target1 var2)
			(target2 var2)
			(target3 var2)
			(target1 var3)
			(target2 var3)
			(target3 var3)
			(target1 var4)
			(target2 var4)
			(target3 var4)
			(target1 var5)
			(target2 var5)
			(target3 var5)
			(target1 var6)
			(target2 var6)
			(target3 var6)
			(target1 var7)
			(target2 var7)
			(target3 var7)
			(target1 var8)
			(target2 var8)
			(target3 var8)
			(target1 var9)
			(target2 var9)
			(target3 var9)
			(target1 var10)
			(target2 var10)
			(target3 var10)
		)
	)
	(:metric minimize (total-time))
)
