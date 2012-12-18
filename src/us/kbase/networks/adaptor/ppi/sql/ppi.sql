# temporary protein interaction schema,
# awaiting integration into CDM
#
# 10/25/12 - changed to point to ProteinSequences in CS instead of Features
# 10/2/12 - created by JMC, based on InteractionSchema_v3_Sept2012 by Miriam

drop table if exists interaction_data;
drop table if exists interaction_protein;
drop table if exists interaction;
drop table if exists interaction_dataset;
drop table if exists interaction_detection_type;
drop table if exists tmp_publication;

create table if not exists tmp_publication (
       id varchar(250) not null,
       kbase_id varchar(250) comment 'link to CS Publication table, if known',
       link varchar(250) comment 'doi or pubmed url; e.g., pubmed:12345',
       primary key (id)
) comment='Local stand-in for Publication table' engine=innodb;

create table if not exists interaction_detection_type (
       id integer unsigned not null auto_increment,
       description varchar(250) not null comment 'description of annotation/detection method',
       primary key (id),
       unique key (description)
) comment='Annotation/Detection method' engine=innodb;

create table if not exists interaction_dataset (
       id integer unsigned not null auto_increment,
       description varchar(250) not null comment 'Description of this dataset',
       data_source text comment 'optional external source; e.g., MO',
       data_url text comment 'optional URL for more info',
       primary key (id),
       unique key (description)
) comment='Interaction data set; i.e., a set of interactions found using one experiment' engine=innodb;

create table if not exists interaction (
       id integer unsigned not null auto_increment,
       interaction_dataset_id integer unsigned not null,
       description varchar(250) not null comment 'description of this interaction (unique within dataset)',
       is_directional boolean not null comment 'true for directional, false for non-directional; bidirectional should be encoded as 2 interactions',
       confidence double comment 'optional numeric estimate of confidence in annotation',
       detection_method_id integer unsigned comment 'optional detection/annotation method',
       data_url text comment 'optional URL for more info',
       citation_id varchar(250) comment 'link to publication',
       primary key (id),
       unique key (interaction_dataset_id, description),
       foreign key (interaction_dataset_id) references interaction_dataset(id) on delete cascade on update cascade,
       foreign key (detection_method_id) references interaction_detection_type(id) on delete cascade on update cascade,
       foreign key (citation_id) references tmp_publication(id) on delete cascade on update cascade
) comment='Interaction - Protein complex or pairwise interaction' engine=innodb;

create table if not exists interaction_protein (
       id integer unsigned not null auto_increment,
       interaction_id integer unsigned not null,
       protein_id varchar(250) not null,
       feature_id varchar(250) not null,
       stoichiometry integer unsigned comment 'if applicable',
       strength double comment 'optional numeric measure of strength',
       rank integer unsigned comment 'numbered starting with 1 within interaction, if proteins are ordered',
       primary key (id),
       foreign key (interaction_id) references interaction(id) on delete cascade on update cascade
       # foreign key (protein_id) references ProteinSequence(id) on delete cascade on update cascade,
       # foreign key (feature_id) references Feature(id) on delete cascade on update cascade,
) comment='Link betweeen Proteins and Interactions' engine=innodb;

create table if not exists interaction_data (
       id integer unsigned not null auto_increment,
       interaction_protein_id integer unsigned not null,
       description text not null comment 'type of data',
       data text not null comment 'the data itself',
       primary key (id),
       foreign key (interaction_protein_id) references interaction_protein(id) on delete cascade on update cascade
) comment='Additional data linked to particular interaction proteins' engine=innodb;
