f = open('/tmp/sampleInput.txt','w')
for user in range (1, 9):
	userId = 'user' + str(user)
	for x in range (1, 9999999):		
		if (x % 2 != 0):
			op = 'open'
		else:
			op = 'close'	
		f.write(userId + ',' + str(x) + ',' + op + '\n')
f.close()
