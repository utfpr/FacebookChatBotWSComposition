
    alter table Emprestimo 
        drop 
        foreign key FKACDD1DCF605CF9BE

    alter table Emprestimo 
        drop 
        foreign key FKACDD1DCF4E4F41BE

    drop table if exists Disciplina

    drop table if exists Emprestimo

    drop table if exists Livro

    drop table if exists Professor

    drop table if exists Sala

    drop table if exists Turma

    drop table if exists Usuario

    create table Disciplina (
        id bigint not null auto_increment,
        bloqueado bit not null,
        dataCadastro datetime,
        nomeDisciplina varchar(255),
        originalId varchar(255),
        siglaDisciplina varchar(255),
        primary key (id)
    )

    create table Emprestimo (
        id bigint not null auto_increment,
        bloqueado bit not null,
        dataCadastro datetime,
        dataDevolucao datetime,
        dataEmprestimo datetime,
        livro_id bigint,
        usuario_id bigint,
        primary key (id)
    )

    create table Livro (
        id bigint not null auto_increment,
        bloqueado bit not null,
        dataCadastro datetime,
        autor varchar(255),
        isbn varchar(255),
        nome varchar(255),
        primary key (id)
    )

    create table Professor (
        id bigint not null auto_increment,
        bloqueado bit not null,
        dataCadastro datetime,
        apelido varchar(255),
        email varchar(255),
        messengerId varchar(255),
        nome varchar(255),
        originalId varchar(255),
        primary key (id)
    )

    create table Sala (
        id bigint not null auto_increment,
        bloqueado bit not null,
        dataCadastro datetime,
        capacidade varchar(255),
        nome varchar(255),
        originalId varchar(255),
        sigla varchar(255),
        primary key (id)
    )

    create table Turma (
        id bigint not null auto_increment,
        bloqueado bit not null,
        dataCadastro datetime,
        nome varchar(255),
        originalID varchar(255),
        sigla varchar(255),
        primary key (id)
    )

    create table Usuario (
        id bigint not null auto_increment,
        bloqueado bit not null,
        dataCadastro datetime,
        apelido varchar(255),
        email varchar(255),
        messegerId varchar(255) unique,
        nome varchar(255),
        ra varchar(255),
        primary key (id)
    )

    alter table Emprestimo 
        add index FKACDD1DCF605CF9BE (usuario_id), 
        add constraint FKACDD1DCF605CF9BE 
        foreign key (usuario_id) 
        references Usuario (id)

    alter table Emprestimo 
        add index FKACDD1DCF4E4F41BE (livro_id), 
        add constraint FKACDD1DCF4E4F41BE 
        foreign key (livro_id) 
        references Livro (id)
