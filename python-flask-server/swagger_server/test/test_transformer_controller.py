# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.error_msg import ErrorMsg  # noqa: E501
from swagger_server.models.gene_info import GeneInfo  # noqa: E501
from swagger_server.models.transformer_info import TransformerInfo  # noqa: E501
from swagger_server.models.transformer_query import TransformerQuery  # noqa: E501
from swagger_server.test import BaseTestCase


class TestTransformerController(BaseTestCase):
    """TransformerController integration test stubs"""

    def test_transform_post(self):
        """Test case for transform_post

        
        """
        query = TransformerQuery()
        response = self.client.open(
            '/common_to_rare_disease/transform',
            method='POST',
            data=json.dumps(query),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_transformer_info_get(self):
        """Test case for transformer_info_get

        Retrieve transformer info
        """
        response = self.client.open(
            '/common_to_rare_disease/transformer_info',
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
