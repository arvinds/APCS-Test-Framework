require 'fileutils'

puts ' running run_tests_if_needed.sh'
base_path = '/var/www/bigbluebutton-default/Dropbox/WoodsideAPCS2012-2013/*'

def generateHtmlReport(numTests, numFails, errors, studentName, assignmentName, fullReport)
	
	report = ["<html>",
			"<head>",
				"<title>",
					"#{assignmentName} Test Report",
				"</title>",
					"<style type='text/css'>",
						".Report{",
							"font-family:Segoe UI,Tahoma,Verdana,Arial,sans-serif;",
							"font-size: 1em;",
							"color: #444444;",
							"line-height:normal;",
							"margin: 8px;",
							"width: 60em;",
						"}",
						".Report A { color: #0066cc; text-decoration: none; }",
						".Report A:hover { text-decoration: underline; }",
						".Report H1 { margin-bottom: 16px; }",
						".Report H2 { color: #000; font-weight: bold; font-size: 160%; }",
						".Report OL { padding-left: 1.5em; }",
						".Report LI { padding-bottom:12px; }",
						".ParagraphHeader {color: #000; font-weight: bold; }",
						".Paragraph { padding: 8px 0px; }",
						".LastParagraph { padding: 24px 0px; }",
						".Footer { color: #888888; font-size: 80%; border-top: 1px solid #cccccc; }",
						".Footer DIV { padding: 4px 0px; }",
						"table.pretty {",
							"margin: 1em 1em 1em 2em;",
							"background: whitesmoke;",
							"border-collapse: collapse;",
						"}",
						"table.pretty th, table.pretty td {",
							"border: 1px gainsboro solid;",
							"padding: 0.2em;",
						"}",
						"table.pretty th {",
							"background: gainsboro;",
							"text-align: left;",
						"}",
						"table.pretty caption {",
							"margin-left: inherit;",
							"margin-right: inherit;",
						"}",
					"</style>",
			"</head>",
			"<body>",
					"<div class='Report'>",
						"<h2>Test Report</h2>",
						"<div class='Paragraph'><span class='ParagraphHeader'>#{assignmentName}</span> Test Report For <span class='ParagraphHeader'>#{studentName}</span> </div>",
						"<div class='Paragraph'>There were <span class='ParagraphHeader'>#{numTests} tests</span> run and <span class='ParagraphHeader'>#{numFails} failures</span>.</div>"]


	if errors.size > 0
				report.push("<table class='pretty'>")
				report.push("<tr><th>#</th><th>Test Name</th><th>Error Type</th><th>Error Details</th><th>Expected Output</th><th>Actual Output</th></tr>")
		errors.each do |error|
			report += [		"<tr>",
							"<td>#{error[0]}</td>",
							"<td>#{error[1]}</td>",
							"<td>#{error[2]}</td>",
							"<td>#{error[3]}</td>",
							"<td>#{error[4]}</td>",
							"<td>#{error[5]}</td>",
						"</tr>"]
		end
				report.push("</table>")
	else
				report.push("<div class='ParagraphHeader'>Great work!</div>")
	end
	report += [			"<div class='LastParagraph'>If you have questions about this report, please email Mr. S(arvindshrihari@outlook.com) or Ms. Caulley(alyssacaulley+whs@gmail.com).</div>",
					"<div class='Footer'>",
						"<div>The full error reprot is below for debuggging help:</div>",
						"<div>",
							"#{fullReport.gsub('\n','<br/><br/>').gsub('\r','<br/><br/>').gsub('\r\n','<br/><br/>')}",
						"</div>",
					"</div>",
				"</div>",
			"</body>",
		"</html>"]
	return report.join()
end

def junitReportToHtmlReport(report, studentName, assignmentName)
	isok = report.scan(/OK \((\d*+) tests\)/m)
	unless isok.empty?
		#no errors
		return generateHtmlReport(isok[0][0].to_i, 0, [], studentName, assignmentName, report)
	else
		#errors
		matches = report.match /Tests run: (\d+),  Failures: (\d+)/m #match tests run and failurse
		numTests = matches[1]
		numFails = matches[2]

		errors = []	
		matches = report.scan(/(\d+)\) (.+?)\(.+?\)(.+?): (.*?)expected:(.*?) but was:(.*?)at .*?.java:\d+\)/m) #match error info
		puts report
		puts "its: " + matches.to_s
		puts "also: " + matches.inspect
		matches.each do |match|
			errorNum = match[0]
			testName = match[1]
			errorType = match[2]
			moreErrorDetails = match[3]

			evMatches = match[4].scan(/<(.*?)>/m)
			expectedValue = evMatches[0][0].strip
			#puts "ev: #{expectedValue}"
			expectedValue = expectedValue.gsub("[","<b>")
			#puts "ev: #{expectedValue}"
			expectedValue = expectedValue.gsub("]", "</b>")
			#puts "ev: #{expectedValue}"

			avMatches = match[5].scan(/<(.*?)>/m)
			actualValue = avMatches[0][0].strip
			#puts "av: #{actualValue}"
			actualValue = actualValue.gsub("[","<b>")
			#puts "av: #{actualValue}"
			actualValue = actualValue.gsub("]", "</b>")
			#puts "av: #{actualValue}"

			errors.push [errorNum, testName, errorType, moreErrorDetails, expectedValue, actualValue]
		end

		return generateHtmlReport(numTests, numFails, errors, studentName, assignmentName, report)

	end

end



Dir[base_path].entries.keep_if{|x| File.directory? x}.each do |studentDir|

	#puts "1" #+ studentDir
	studentName = studentDir.split('/').last

	Dir[studentDir + "/*"].entries.keep_if{|x| File.directory? x}.each do |assignmentDir|

		#puts "2"# + assignmentDir
		assignmentName = assignmentDir.split('/').last

		Dir[assignmentDir + "/*"].entries.keep_if{|x| File.directory?(x) && x =~ /done$/}.each do |submissionDir|

			#puts "3" + assignmentDir
			submissionDirName = submissionDir.split('/').last
			#puts '3.5' + submissionDir + "/*"
			#puts '4' + Dir[submissionDir + "/*"].entries.to_s
			if Dir[submissionDir + "/*"].entries.keep_if{|x| !File.directory?(x) && x =~ /.html$/}.size == 0
				#need to run tests
				#puts assignmentName
				result = `/root/woodside_apcs_2013_tests/APCS-Test-Framework/scripts/run_test.sh  #{studentName} #{assignmentName} #{submissionDirName} 2>&1`
				puts result
				File.open(submissionDir + '/result.html', "w") do |f|
					if(result =~ /JUnit version 4.11-SNAPSHOT-/)
						matches = (/JUnit version 4.11.+?\n(.*)/m).match result
						htmlPart = junitReportToHtmlReport(matches[1], studentName, assignmentName)
						f.puts htmlPart
					else
						f.puts "<html><head><title>Test Report</title></head><body><h1>Oops</h1>There was an error running the test.<br/>Please make sure you've followed the directions for submitting the assignment including having the right class names and file names.<br/><br/>Please email Mr S. (arvindshrihari@outlook.com) or Ms. Caulley (alyssacaulley+whs@gmail.com) with a copy of the text below.<br/><br/>Here is what happened when we tried to run the test:<br/><i>"
						f.puts result.gsub("\n","<br/>")
						f.puts "</i></body></html>"
					end
				end
			end
		end
	end
end
