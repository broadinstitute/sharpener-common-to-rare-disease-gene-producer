#!/usr/bin/perl -w

use File::Basename;
use JSON -support_by_pp;

$dirname   = dirname(__FILE__);
$json      = new JSON;
$python3   = "python3";
$diseaseID = $ARGV[0];

#get disease -> symptoms
open(IN, "$python3 $dirname/diseasesToSymptomps.py $diseaseID | tail -1 |") || die;
$diseaseJson = <IN>;
$disease = $json->allow_nonref->utf8->relaxed->escape_slash->loose->allow_singlequote->decode($diseaseJson);

foreach $symptomHit (@{$disease->[0]->{"hpo"}->{"phenotype_related_to_disease"}}) {
	next if !exists $symptomHit->{"hpo_id"};
	next if "HP:0000006" eq $symptomHit->{"hpo_id"};
	$allSymptoms{$symptomHit->{"hpo_id"}} = 1;
}

#get symptoms -> diseases
@allSymptoms = keys %allSymptoms;
open(IN, "$python3 $dirname/symptomsToDiseases.py @allSymptoms | tail -1 |") || die;
$symptomsJson = <IN>;
$symptoms = $json->allow_nonref->utf8->relaxed->escape_slash->loose->allow_singlequote->decode($symptomsJson);

foreach $diseaseHit (@{$symptoms}) {
	next if !exists $diseaseHit->{"query"};
	next if !exists $diseaseHit->{'mondo'};
	next if !exists $diseaseHit->{'mondo'}->{'xrefs'};
	next if !exists $diseaseHit->{'mondo'}->{'xrefs'}->{'omim'};
	next if $diseaseID == $diseaseHit->{'mondo'}->{'xrefs'}->{'omim'};
	$allDiseases{$diseaseHit->{'mondo'}->{'xrefs'}->{'omim'}} = 1;
}

#get diseases -> genes
@allDiseases = sort keys %allDiseases;
open(IN, "$python3 $dirname/diseasesToGenes.py @allDiseases | tail -1 |") || die;
$genesJson = <IN>;

$genes = $json->allow_nonref->utf8->relaxed->escape_slash->loose->allow_singlequote->decode($genesJson);

foreach $geneHit (@{$genes}) {
	next if !exists $geneHit->{"query"};
	next if !exists $geneHit->{"disgenet"};
	if(ref($geneHit->{"disgenet"}) eq "HASH") {
		next if !exists $geneHit->{"disgenet"}->{"genes_related_to_disease"};
		if(ref($geneHit->{"disgenet"}->{"genes_related_to_disease"})  eq "HASH") {
			next if !exists $geneHit->{"disgenet"}->{"genes_related_to_disease"}->{"gene_id"}; 
			$diseases2genes{$geneHit->{"query"}}{$geneHit->{"disgenet"}->{"genes_related_to_disease"}->{"gene_id"}} = 1;
			$allGenes{$geneHit->{"disgenet"}->{"genes_related_to_disease"}->{"gene_id"}} = 1;
			
		}
		else {
			foreach $geneHitRelatedToDisease (@{$geneHit->{"disgenet"}->{"genes_related_to_disease"}}) {
				next if !exists $geneHitRelatedToDisease->{"gene_id"};
				$diseases2genes{$geneHit->{"query"}}{$geneHitRelatedToDisease->{"gene_id"}} = 1;
				$allGenes{$geneHitRelatedToDisease->{"gene_id"}} = 1;
			}
		}
	}
	else {
		foreach $myDisgenet (@{$geneHit->{"disgenet"}}) {
			next if exists $myDisgenet->{"genes_related_to_disease"};
			if(ref($myDisgenet->{"genes_related_to_disease"}) eq "HASH") {
				$diseases2genes{$geneHit->{"query"}}{$geneHit->{"disgenet"}->{"genes_related_to_disease"}->{"gene_id"}} = 1;
				$allGenes{$geneHit->{"disgenet"}->{"genes_related_to_disease"}->{"gene_id"}} = 1;
			}
			else {
				foreach $geneHitRelatedToDisease (@{$myDisgenet->{"genes_related_to_disease"}}) {
					$diseases2genes{$geneHit->{"query"}}{$geneHitRelatedToDisease->{"gene_id"}} = 1;
					$allGenes{$geneHitRelatedToDisease->{"gene_id"}} = 1;
				}
			}
		}
	}
}

foreach(sort {$a<=>$b} keys %allGenes) {
	print $_,"\n";
}

#print output
#foreach(sort keys %symptomIDs) { push @json, $_; }
#print JSON->new->pretty->encode(\@json);
