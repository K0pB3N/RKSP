// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

contract StockService {
    bool public isInit; 

    address public owner;

    struct Stock {
        string name;
        uint cost;
    }

    struct UserStockInfo {
        Stock stock;
        uint count;
    }

    Stock[] public arrStocks;
    
    mapping (address => UserStockInfo[]) public mapUserSrocksInfo;

    constructor() {
        owner = msg.sender;
        isInit = false;
    }

    event SentMoney(address _to, uint cost);

    function init(address[] calldata users, string[] calldata arrStockNames, uint[] calldata counts) public {
        require(msg.sender == owner, "Only owner");
        require(!isInit, "Already initialized");

        isInit = true;

        arrStocks.push(Stock("apple", 100000));
        arrStocks.push(Stock("ibm", 60000));
        arrStocks.push(Stock("microsoft", 90000));
        arrStocks.push(Stock("samsung", 40000));
        
        for(uint i = 0; i < arrStockNames.length && i < counts.length && i < users.length; i++) {
            uint stockIndex;
            bool hasError;

            (stockIndex, hasError) = getStockIndexByName(arrStockNames[i]);

            require(!hasError, "Not found stock by name");

            UserStockInfo memory info = UserStockInfo(arrStocks[stockIndex], counts[i]);
            mapUserSrocksInfo[users[i]].push(info);
        }
    }

    modifier withIsInit() {
        require(isInit, "Not init yet");
        _;
    }

    modifier withHasBuyerMoney(string calldata stockName, uint count) {
        require(hasBuyerMoney(stockName, count), "Not enough money");
        _;
    }

    function getStockIndexByName(string calldata stockName) public view returns(uint stockIndex, bool hasError) {
        for (uint i = 0; i < arrStocks.length; i++) {
            if (isEqualString(stockName, arrStocks[i].name)) {
                return (i, false);
            }
        }

        return (0, true);
    }
    
    function getUserStockInfoIndex(address salesman, string calldata stockName) public view withIsInit() returns(uint infoIndex, bool hasError) {
        for (uint i = 0; i < mapUserSrocksInfo[salesman].length; i++) {
            if (isEqualString(mapUserSrocksInfo[salesman][i].stock.name, stockName)) {
                return (i, false);
            }
        }

        return (0, true);
    }

    function buy(address payable salesman, string calldata stockName, uint count) public payable
        withIsInit()
        withHasBuyerMoney(stockName, count)
    {
        uint cost = setUserStockInfo(salesman, msg.sender, stockName, count);
        sentMoney(salesman, cost);
    }

    function hasBuyerMoney(string calldata stockName, uint count) private withIsInit() returns(bool) {
        uint stockIndex;
        bool hasError;

        (stockIndex, hasError) = getStockIndexByName(stockName);

        return !hasError && msg.value >= arrStocks[stockIndex].cost * count;
    }

    function setUserStockInfo(address salesman, address sender, string calldata stockName, uint count) private returns (uint) {
        uint infoIndex;
        bool hasError;

        (infoIndex, hasError) = getUserStockInfoIndex(salesman, stockName);

        require(!hasError && mapUserSrocksInfo[salesman][infoIndex].count >= count, "The seller does not have enough stocks");

        mapUserSrocksInfo[salesman][infoIndex].count -= count;

        uint infoIndexSender;
        bool notFoundStock;

        (infoIndexSender, notFoundStock) = getUserStockInfoIndex(sender, stockName);

        if (notFoundStock) {
            uint stockIndex;
            bool notFound;
            
            (stockIndex, notFound) = getStockIndexByName(stockName);

            UserStockInfo memory info = UserStockInfo(arrStocks[stockIndex], count);
            mapUserSrocksInfo[sender].push(info);
        } else {
            mapUserSrocksInfo[sender][infoIndexSender].count += count;
        }

        return count * mapUserSrocksInfo[salesman][infoIndex].stock.cost;
    }

    function sentMoney(address payable salesman, uint cost) private {
        emit SentMoney(salesman, cost);

        salesman.transfer(cost);

        address payable payableSender = payable(msg.sender);

        emit SentMoney(msg.sender, address(this).balance);
    
        payableSender.transfer(address(this).balance);
    }

    // https://stackoverflow.com/questions/54499116/how-do-you-compare-strings-in-solidity
    function isEqualString(string memory str1, string memory str2) private pure returns(bool) {
        return keccak256(abi.encodePacked(str1)) == keccak256(abi.encodePacked(str2));
    }
}
