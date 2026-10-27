# Exemplos de consulta - Chamados

| Objetivo | Requisição |
|---|---|
| Listar todos (10 por página, mais novos primeiro) | GET /chamados |
| Filtrar por status | GET /chamados?status=ABERTO |
| Filtrar por prioridade | GET /chamados?prioridade=ALTA |
| Filtrar por cliente | GET /chamados?clienteId=1 |
| Combinar filtros | GET /chamados?status=ABERTO&prioridade=ALTA&clienteId=1 |
| Paginar | GET /chamados?page=0&size=5 |
| Ordenar | GET /chamados?sort=dataAbertura,asc |
| Chamados de um cliente | GET /clientes/1/chamados |

Valores de status: ABERTO, EM_ATENDIMENTO, AGUARDANDO_CLIENTE, FECHADO.
Valores de prioridade: BAIXA, MEDIA, ALTA, CRITICA.