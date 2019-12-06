import connexion
import six

from swagger_server.models.error_msg import ErrorMsg  # noqa: E501
from swagger_server.models.gene_info import GeneInfo  # noqa: E501
from swagger_server.models.transformer_info import TransformerInfo  # noqa: E501
from swagger_server.models.transformer_query import TransformerQuery  # noqa: E501
from swagger_server import util


def transform_post(query):  # noqa: E501
    """transform_post

     # noqa: E501

    :param query: Performs transformer query.
    :type query: dict | bytes

    :rtype: List[GeneInfo]
    """
    if connexion.request.is_json:
        query = TransformerQuery.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def transformer_info_get():  # noqa: E501
    """Retrieve transformer info

    Provides information about the transformer. # noqa: E501


    :rtype: TransformerInfo
    """
    return 'do some magic!'
