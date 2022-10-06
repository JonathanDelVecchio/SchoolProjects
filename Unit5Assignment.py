
###
import sys,os,re
import time, math, sqlite3
#
# Global variables
#
documents = 0
term = {}
queries = []
terms = 0
resultQuery = []
#
# Importing Porter Stemmer from file portstemmer.py and creating stemmer object
from porterstemmer import PorterStemmer
stemmer = PorterStemmer()
#
# Routine to identify stop words
# Stopwords imported from https://www.geeksforgeeks.org/removing-stop-wordsnltk-python/
#
stopwords = {"ourselves", "hers", "between", "yourself", "but", "again",
"there", "about", "once", "during", "out", "very", "having", "with", "they",
"own", "an", "be", "some", "for", "do", "its", "yours", "such", "into", "of",
"most", "itself", "other", "off", "is", "s", "am", "or", "who", "as", "from",
"him", "each", "the", "themselves", "until", "below", "are", "we", "these",
"your", "his", "through", "don", "nor", "me", "were", "her", "more",
"himself", "this", "down", "should", "our", "their", "while", "above", "both",
"up", "to", "ours", "had", "she", "all", "no", "when", "at", "any", "before",
"them", "same", "and", "been", "have", "in", "will", "on", "does",
"yourselves", "then", "that", "because", "what", "over", "why", "so", "can",
"did", "not", "now", "under", "he", "you", "herself", "has", "just", "where",
"too", "only", "myself", "which", "those", "i", "after", "few", "whom", "t",
"being", "if", "theirs", "my", "against", "a", "by", "doing", "it", "how",
"further", "was", "here", "than", "pre", "html"}
def TestStop(word):
 if not word in stopwords:
     return True
 else:
     return False
#
# split on space
#
def splitchars(line):
 return line.split()
#
# process the tokens of the query
#
def parsetoken(line):

 global documents
 global term
 global queries
 global terms

 # this replaces any tab characters with a space character in the line
 # read from the file
 line = line.replace('\t',' ')
 line = line.strip()
 # This routine splits the contents of the line into tokens
 l = splitchars(line)
 # for each token in the line process
 for elmt in l:
     elmt = elmt.replace('\n','')
     lowerElmt = elmt.lower().strip()
     if not lowerElmt.isalnum():
         continue
     if not TestStop(lowerElmt):
         continue
stemmedElmt = stemmer.stem(lowerElmt, 0, len(lowerElmt)-1)

 #print("Token %s" % stemmedElmt)
terms += 1
 # Getting the df for the current term
term = {}
cur.execute("SELECT * FROM TermDictionary WHERE Term = ?",
            (stemmedElmt,))
token = cur.fetchone()
if token is None:
 print("Term %s not found." % elmt)
else:
 #print("Token %s, Document frequency %d in %i documents" % (stemmedElmt, token[2], documents))
    idf = math.log(documents/token[2])
    term["Term"] = stemmedElmt
    term["TermID"] = token[1]
    term["DocFreq"] = token[2]
    term["idf"] = idf
    queries.append(term)


if __name__ == "__main__":

#
# Create a sqlite database to hold the inverted index.
# Database is created in same folder where the script is located
#
 con = sqlite3.connect("./indexer_unit4.db")
 con.isolation_level = None
 cur = con.cursor()
#
# Count documents
#
 cur.execute("SELECT * FROM DocumentDictionary")
 documents = len(cur.fetchall())

#
# Get the entry from user
#
 line = input('Enter the search terms, each separated by a space: ')
#
# Capture the start time of the routine so that we can determine the total
running
# time required to process the corpus
#
t2 = time.localtime()
print("Processing Start Time: %.2d:%.2d:%.2d" % (t2.tm_hour, t2.tm_min,
t2.tm_sec)) # Added seconds

#
# Parse search query
#
parsetoken(line)
#
# Test print line
#
 #print("Line: %s" % line)
 #print(queries)
#
# Calculate cosine similarity between documents and
#
for each in queries:
 #print(each.get("DocFreq"))
 tf_idf = each.get("idf")/(1/terms)
 cur.execute("SELECT * FROM Posting WHERE TermId = ?",
(each.get("TermID"),))
 docs = cur.fetchall()
 for line in docs:
     cur.execute("SELECT DocFreq FROM TermDictionary WHERE TermId = ?",
                 (line[0],))
 doc_idf = cur.fetchone()[0]
 dot = tf_idf * each.get("idf")
 quer = abs(tf_idf)
 doc = abs(doc_idf)
 similarity = (dot/quer*doc)/documents
 result = {}
 result["DocID"] = line[1]
 result["Sim"] = similarity
 resultQuery.append(result)

#
# Print results
#
 k = 20
 for item in resultQuery[:k]:
     cur.execute("SELECT DocumentName FROM DocumentDictionary WHERE DocId =?", (item.get("DocID"),))
     name = docs = cur.fetchall()[0]
 print("Document: %s have similarity score %f, from %i documents." %(name, item.get("Sim"), documents))
 t2 = time.localtime()
 print("Processing End Time: %.2d:%.2d:%.2d" % (t2.tm_hour, t2.tm_min, t2.tm_sec)) 
