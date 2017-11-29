package com.wang.test

class TimeEscape {
	val start = System.currentTimeMillis() 
	
	def elapsedTime() = {
        val now = System.currentTimeMillis();
        (now - start) / 1000.0;
    }
	
	
	
}