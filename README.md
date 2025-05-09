This repository contains an implementation of PolyProtect ([paper](https://arxiv.org/abs/2110.00434), [code](https://gitlab.idiap.ch/bob/bob.paper.polyprotect_2021)), a Biometric Template Protection (BTP) algorithm, in the form of an Android library (in Kotlin).

In a nutshell, PolyProtect combines subject-specific secret information (also called auxiliary data) with face templates to create a protected version of them. The auxiliary data are generated during the enrollment and they are used during the operations of verification and identification. 
The auxiliary data consist of two parts: the coefficients (an array of signed integers excluding zero) and the exponents (an array of positive signed integers). The two arrays have the same length and they are used to perform a polynomial transformation on several slices of the input (unprotected) face template. 
The output of this transformation is the protected template.

# PolyProtect configuration

- ```polynomialDegree: Int = 7```: degree of the polynomial used in the PolyProtect transformation. The minimum value which would provide any security is 5, since there is no closed form algebraic expression for solving polynomials of degree 5 or higher with arbitrary coefficients.
  <ins>This variable has an important impact on the system design as it defines the maximum number of subjects that can be enrolled with different sets of exponents</ins>, which are *half* of the secret on which PolyProtect relies (the other half would be given by the polynomial coefficients). Such number is computed as the factorial of ```polynomialDegree```. For example, if ```polynomialDegree = 7```, then ```polynomialDegree!``` = 5040. That is, with ```polynomialDegree``` being 7 the maximum number of subjects that can be enrolled in the database is 5040. The maximum value for ```polynomialDegree``` is 14. Although, the upper limit of ```polynomialDegree``` is bound by the fact that the embeddings produced by NN-based feature extractors often consist of floating point values smaller than 1, which would cause large powers to effectively obliterate certain embedding elements during the PolyProtect transformation. However, 14! = 87,178,291,200 which would cover over ten times the entire world population.
  At the bottom of this page it is possible to find a [table of factorials](#table-of-factorials) for the [5,14] range. In the original paper, ```polynomialDegree``` is named *m*.

- ```coefficientAbsMax: Int = 100```: range limit for the coefficients of the polynomial used in the PolyProtect transformation. The coefficients will be drawn from the [-```coefficientAbsMax```, ```coefficientAbsMax```] range excluding 0.

- ```overlap: Int = 2```: overlap of the intervals of the unprotected template to transform using the PolyProtect polynomial transformation. It has an impact on the security _vs._ recognition accuracy tradeoff. Specifically, as you increase the ```overlap```, the accuracy of the system improves and the security of protected template decreases. It can go from 0 to ```polynomialDegree``` - 1. The safest options would be: {0, 1, 2, 3}.

# Workflow

In this section we report the sequence diagrams are included for integrating PolyProtect in the operations of enrolment, verification, and identification.

## Enrolment

During a subject's enrolment, firstly, a set of coefficients and exponents (```AuxData``` data class) are randomly generated by the ```generateAuxData``` method. 
Secondly, it is necessary to check that the same coefficients and exponents have not been assigned to previously enrolled subjects by reading the auxiliary data database, where they are stored.
Once this check is completed, the auxiliary data can be saved in the database. Then, the unprotected template can be transformed with PolyProtect using the ```transformTemplate``` method. The output of this operation is the protected template, which can be saved in a separate database for protected templates.
The unprotected template should NOT be stored.

![alt text](images/Enrolment.png)

**NOTE:** The process of verifying that the coefficients and exponents randomly drawn have not been assigned to any previously enrolled subject does not have to necessarily take place in the form of a while loop.

## Verification

For verification, the ```AuxData``` of the subject whose identity is about to be verified is known. Consequently, we read the auxiliary data database to obtain the corresponding ```AuxData```.
Then, we transform the unprotected query template so that it can be compared directly with the protected reference (enrolment) template with the ```transformTemplate``` method. Consequently, we read the protected template database and we return the protected template.
The matching of the two templates is carried out using the ```computeSimilarityScore``` function, which returns a score in the [0, 1] range, being 1 a perfect match.

![alt text](images/Verification.png)

## Identification 

For identification, the ```AuxData``` of the subject whose identity is about to be verified is not known. Consequently, it is necessary to consider all ```AuxData``` stored. For this reason, we implement a for loop in the sequence diagram below. First, we initialise a list in which the scores and the corresponding ```subjectIds``` can be stored. In each iteration of the for loop, one enrolled subject per time is considered.
Their ```AuxData``` is obtained from the database and used to transform the unprotected query template.
Then, the protected template corresponding to the ```AuxData``` of this iteration is retrieved so that it can be compared directly with the protected query template. 
The matching of the two templates is carried out using the ```computeSimilarityScore``` method, which returns a score in the [0, 1] range, being 1 a perfect match. In each iteration, the score is appended to the previously created list together with the ```subjectId```.
Finally, the list is sorted by the score and the N most similar matches are considered.

![alt text](images/Identification.png)

# Table of factorials

| *x* (corresponding to ```polynomialDegree```) | *x!* (corresponding to maximum number of enrolled subjects) |
|:---------------------------------------------:|:-----------------------------------------------------------:|
|                       5                       |                             120                             |
|                       6                       |                             720                             |
|                       7                       |                            5,040                            |
|                       8                       |                           40,320                            |
|                       9                       |                           362,880                           |
|                      10                       |                          3,628,800                          |
|                      11                       |                         39,916,800                          |
|                      12                       |                         479,001,600                         |
|                      13                       |                        6,227,020,800                        |
|                      14                       |                       87,178,291,200                        |
