const { expect } = require("chai");
const { ethers } = require("hardhat");

async function deployStockService() {
    const StockService = await ethers.getContractFactory("StockService");
    
    const [owner, addr1, addr2, addr3] = await ethers.getSigners();

    const addr1Balance = await addr1.getBalance();
    const addr2Balance = await addr2.getBalance();

    console.log('addr1 balance:'+ addr1Balance);
    console.log('addr2 balance:'+ addr2Balance);

    const stockService = await StockService.deploy();

    return [stockService, addr1, addr2, addr3, owner];
}

async function initSuccess(stockService, addr1, addr2) {
    const users = [addr1.address, addr2.address, addr2.address];
    const arrStockNames = ["ibm", "apple", "microsoft"];
    const counts = [10, 5, 6];

    return stockService.init(users, arrStockNames, counts);
}

describe("StockService", function () {
    describe("init", function () {
        it("init StockService is success", async function () {
            const [stockService, addr1, addr2]  = await deployStockService();

            expect(await stockService.isInit()).to.equal(false);

            await initSuccess(stockService, addr1, addr2);

            const infoAddr1Index0 = await stockService.mapUserSrocksInfo(addr1.address, 0);
            const infoAddr2Index0 = await stockService.mapUserSrocksInfo(addr2.address, 0);
            const infoAddr2Index1 = await stockService.mapUserSrocksInfo(addr2.address, 1);
            
            expect(infoAddr1Index0.count).to.equal(10);
            expect(infoAddr1Index0.stock.name).to.equal("ibm");
            expect(infoAddr1Index0.stock.cost).to.equal(60000);

            expect(infoAddr2Index0.count).to.equal(5);
            expect(infoAddr2Index0.stock.name).to.equal("apple");
            expect(infoAddr2Index0.stock.cost).to.equal(100000);
            
            expect(infoAddr2Index1.count).to.equal(6);
            expect(infoAddr2Index1.stock.name).to.equal("microsoft");
            expect(infoAddr2Index1.stock.cost).to.equal(90000);

            expect(await stockService.isInit()).to.equal(true);
        });

        it("init StockService is error", async function () {
            const [stockService, addr1, addr2]  = await deployStockService();

            expect(await stockService.isInit()).to.equal(false);

            const users = [addr1.address, addr2.address, addr2.address];
            const arrStockNames = ["ibm", "google", "microsoft"];
            const counts = [10, 5, 6];

            await expect(stockService.init(users, arrStockNames, counts)).to.be.revertedWith("Not found stock by name");
            
            expect(await stockService.isInit()).to.equal(false);
        });
    });

    describe("buy", function () {
        it("StockService buy is error not have enough stocks", async function () {
            const [stockService, addr1, addr2]  = await deployStockService();

            await initSuccess(stockService, addr1, addr2);

            const promiseBuy = stockService.connect(addr1).buy(addr2.address, "apple", 7, {
                value: 700000
            });

            await expect(promiseBuy).to.be.revertedWith("The seller does not have enough stocks");
        });

        it("StockService buy is success", async function () {
            const [stockService, addr1, addr2]  = await deployStockService();

            await initSuccess(stockService, addr1, addr2);

            const promiseBuy = stockService.connect(addr1).buy(addr2.address, "apple", 3, {
                value: 300000,
            });

            await promiseBuy;

            const infoAddr1Apple = await stockService.mapUserSrocksInfo(addr1.address, 1);
            const infoAddr2Apple = await stockService.mapUserSrocksInfo(addr2.address, 0);

            expect(infoAddr1Apple.count).to.equal(3);
            expect(infoAddr1Apple.stock.name).to.equal("apple");

            expect(infoAddr2Apple.count).to.equal(2);
            expect(infoAddr2Apple.stock.name).to.equal("apple");
        });

        it("StockService buy is error not enough money", async function () {
            const [stockService, addr1, addr2]  = await deployStockService();

            await initSuccess(stockService, addr1, addr2);

            const promiseBuy = stockService.connect(addr1).buy(addr2.address, "apple", 1, {
                value: 10
            });

            await expect(promiseBuy).to.be.revertedWith("Not enough money");
        });
    });
});
