# Data Warehouse Statistical Analysis
Perform statistical analysis of bio-medical data using F-test, T-test, Pearson Correlation and to help perform knowledge discovery like finding information genes and classifying new patients

##Part I
Your data warehouse is supposed to support the regular OLAP operations (e.g., roll-up,
drill down, slice, dice and pivot), as well as some statistical operations (e.g., t-test,
ANOVA, and correlation). In the following are some typical queries by users. You may
use either SQL, PL/SQL, or external programs (e.g. in Java) to answer the queries. Notice
that you should retrieve the data from the Oracle system instead of the original plain text
files. Report your approach and the results returned by your data warehouse.
<ul>
<li>List the number of patients who had “tumor” (disease description), “leukemia” (disease type) and
“ALL” (disease name), separately.
<li>List the types of drugs which have been applied to patients with “tumor”.
<li>For each sample of patients with “ALL”, list the mRNA values (expression) of probes in cluster
id “00002” for each experiment with measure unit id = “001”. (Note: measure unit id corresponds
to mu_id in microarray_fact.txt, cluster id corresponds to cl_id in gene_fact.txt, mRNA expression
value corresponds to exp in microarray_fact.txt, UID in probe.txt is a foreign key referring to
gene_fact.txt)
<li>For probes belonging to GO with id = “0012502”, calculate the t statistics of the expression values
between patients with “ALL” and patients without “ALL”. (Note: Assume the expression values
of patients in both groups have equal variance, use the t test for unequal sample size, equal
variance)
<li>For probes belonging to GO with id=“0007154”, calculate the F statistics of the expression values
among patients with “ALL”, “AML”, “colon tumor” and “breast tumor”. (Note: Assume the
variances of expression values of all four patient groups are equal.)
<li>For probes belonging to GO with id=“0007154”, calculate the average correlation of the
expression values between two patients with “ALL”, and calculate the average correlation of the
expression values between one “ALL” patient and one “AML” patient. (Note: For each patient,
there is a list of gene expression values belonging to GO with id=“0007154”. Suppose you get
N 1 “ALL” patients and N 2 “AML” patient. For the average correlation of the expression
values between two patients with “ALL”, you need first calculate
N 1 ×(N 1 −1)/2 PersonCorrelations then calculate the average value. For the average correlation of the expression values
between one “ALL” patient and one “AML” patient, you need first calculate N 1 × N 2 Person
Correlations then calculate the average value.)
</ul>

##Part II
Use your data warehouse and the OLAP operations to support knowledge discovery.
<ol>
<li>Given a specific disease, find the informative genes.
For example, suppose we are interested in the cancer “ALL”.
<ol>
<li>Find all the patients with “ALL” (group A), while the other patients serve as the
control (group B).
<li> For each gene, calculate the t-statistics for the expression values between group A
and group B.
<li> If the p-value of the t-test is smaller than 0.01, this gene is regarded as an
“informative” gene.
</ol>
<li> Use informative genes to classify a new patient (five test cases in test_samples.txt are
given in the data).
For example, given a new patient P N , we want to predict whether he/she has “ALL”.
<ol>
<li>Find the informative genes w.r.t. “ALL”.
<li>Find all the patients with “ALL” (group A).
<li>For each patient P A in group A, calculate the correlation r A of the expression
values of the informative genes between P N and P A .
<li>Patients without “ALL” serve as the control (group B).
<li>For each patient P B in group B, calculate the correlation r B of the expression
values of the informative genes between P N and P B .
<li>Apply t-test on r A and r B , if the p-value is smaller than 0.01, the patient is
classified as “ALL”.
